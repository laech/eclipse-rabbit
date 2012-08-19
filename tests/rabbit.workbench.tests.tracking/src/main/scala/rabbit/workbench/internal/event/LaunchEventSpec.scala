/*
 * Copyright 2012 The Rabbit Eclipse Plug-in Project
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

package rabbit.workbench.internal.event

import java.util.Collections.emptySet
import java.util.Set

import scala.collection.JavaConversions.setAsJavaSet

import org.eclipse.core.runtime.{ Path, IPath }
import org.eclipse.debug.core.{ ILaunchConfigurationType, ILaunchConfiguration, ILaunch }
import org.joda.time.Instant.now
import org.joda.time.{ Instant, Duration }
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar.mock

import rabbit.tracking.event.EventSpec
import rabbit.tracking.tests.TestUtils.{ epoch, duration }
import rabbit.tracking.tests.{ FinalSpecBase, EqualsSpecBase }

@RunWith(classOf[JUnitRunner])
final class LaunchEventSpec extends EventSpec with EqualsSpecBase with FinalSpecBase {

  behavior of clazz.getSimpleName

  it must "return the files" in {
    val fs = fileSet("/a/b", "/c/d")
    event(files = fs).files must be(fs)
  }

  it must "return the launch" in {
    val l = mockLaunch
    val e = event(launch = l)
    e.launch must be(l)
    e.launchConfig must be(l.getLaunchConfiguration)
    e.launchConfigType must be(l.getLaunchConfiguration.getType)
  }

  it must "throw NullPointerException if constructing without a file set" in {
    intercept[NullPointerException] {
      event(files = null)
    }
  }

  it must "throw NullPointerException if constructing without a launch" in {
    intercept[NullPointerException] {
      event(launch = null)
    }
  }

  it must "throw NullPointerException if constructing without a launch configuration" in {
    val l = mockLaunch
    given(l.getLaunchConfiguration).willReturn(null)
    intercept[NullPointerException] {
      event(launch = l)
    }
  }

  it must "throw NullPointerException if constructing without a launch configuration type" in {
    val l = mockLaunch
    given(l.getLaunchConfiguration.getType).willReturn(null)
    intercept[NullPointerException] {
      event(launch = l)
    }
  }

  private val launch = mockLaunch
  private val files = emptySet[IPath]

  override protected def clazz = classOf[LaunchEvent]

  override protected def differences = Table(
    ("event a", "event b"),
    (event(), event(instant = now)),
    (event(), event(duration = duration(101))),
    (event(), event(launch = mockLaunch)),
    (event(), event(files = fileSet("/a/b.txt"))))

  override protected def equalObject() = event()

  override protected def create(instant: Instant) = event(instant = instant)

  /**
   * Creates a default event if no argument is supplied, all default events will
   *  be equivalent to each other.
   */
  private def event(
    instant: Instant = epoch,
    duration: Duration = new Duration(10),
    launch: ILaunch = launch,
    files: Set[IPath] = files) = new LaunchEvent(instant, duration, launch, files)

  private def mockLaunch() = {
    val configType = mock[ILaunchConfigurationType]
    val config = mock[ILaunchConfiguration]
    given(config.getType).willReturn(configType)

    val launch = mock[ILaunch]
    given(launch.getLaunchConfiguration).willReturn(config)

    launch
  }

  private def fileSet(paths: String*): Set[IPath] = setAsJavaSet(paths.map(new Path(_)).toSet)

}