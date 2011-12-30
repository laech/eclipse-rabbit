/*
 * Copyright 2011 The Rabbit Eclipse Plug-in Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package rabbit.tracking.internal

import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter
import org.osgi.framework.BundleActivator
import org.osgi.framework.BundleContext
import org.osgi.framework.Bundle
import org.osgi.framework.Bundle._
import org.scalatest.Spec
import org.scalatest.OneInstancePerTest
import rabbit.tracking.ITracker
import rabbit.tracking.internal.TrackingPlugin._
import org.eclipse.core.runtime.Platform
import org.eclipse.core.runtime.Platform._
import org.scalatest.matchers.MustMatchers
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.google.common.collect.ImmutableSet

@RunWith(classOf[JUnitRunner])
final class TrackingPluginTest extends Spec
  with BeforeAndAfter with OneInstancePerTest with MustMatchers {

  private val bundle = TrackingPlugin.getDefault.getBundle
  private var plugin: TrackingPlugin = _

  before {
    plugin = TrackingPlugin.getDefault
  }

  after {
    if (bundle.getState != ACTIVE) bundle.start
  }

  describe("A tracking plugin") {

    it("should create trackers from extension point") {
      val method = classOf[TrackingPlugin].getDeclaredMethod("createTrackers")
      method.setAccessible(true)
      val trackers = method.invoke(plugin)
        .asInstanceOf[java.util.Collection[ITracker[_]]]

      val extension = getExtensionRegistry
        .getConfigurationElementsFor(TRACKER_EXTENSION_ID)

      assume(extension.size > 0) // Assume built in trackers are loaded
      expect(extension.size)(trackers.size)
    }

    it("should have an idle detector") {
      plugin.getIdleDetector must not be null
    }

    it("should have the idle detector's idle interval set to 1 minute") {
      plugin.getIdleDetector.getIdleInterval must equal(60000)
    }

    it("should have the idle detector's run delay set to 1 second") {
      plugin.getIdleDetector.getRunDelay must equal(1000)
    }

    it("should have the correct plugin ID") {
      TrackingPlugin.PLUGIN_ID must equal(bundle.getSymbolicName)
    }

    it("should disable trackers before shutdown") {
      bundle.stop
      getTrackers(plugin).filter(_.isEnabled) must have size 0
    }

    it("should tell the tracks to save data when asked") {
      val tracker = TestUtil.newTracker[Any]
      tracker.getData.add(new Object)
      tracker.getData.add(new Object)
      assume(!tracker.getData.isEmpty)

      val field = classOf[TrackingPlugin].getDeclaredField("trackers")
      field.setAccessible(true)
      field.set(plugin, ImmutableSet.of[ITracker[_]](tracker))

      plugin.saveCurrentData
      tracker.getData.isEmpty must be(true)

      // Reset the plug-in
      bundle.stop
      bundle.start
    }

    it("should start the idle detector on startup") {
      plugin.getIdleDetector.isRunning must be(true)
    }

    it("should start the trackers on startup") {
      val trackers = getTrackers(plugin)
      trackers.isEmpty must not be true
      trackers.filter(!_.isEnabled) must have size 0
    }

    it("should stop the idle detector on stop") {
      val detector = plugin.getIdleDetector
      bundle.stop
      detector.isRunning must not be true
    }

    it("should stop the trackers on stop") {
      var trackers = getTrackers(plugin)
      assume(!trackers.isEmpty)
      bundle.stop
      trackers.filter(_.isEnabled) must have size 0
    }

    it("should have the right extension ID") {
      getExtensionRegistry
        .getConfigurationElementsFor(TRACKER_EXTENSION_ID).size must be > 0
    }
  }

  private def getTrackers(plugin: TrackingPlugin): collection.Set[ITracker[_]] = {
    import collection.JavaConversions._
    val field = classOf[TrackingPlugin].getDeclaredField("trackers")
    field.setAccessible(true)
    field.get(plugin).asInstanceOf[java.util.Set[ITracker[_]]]
  }
}