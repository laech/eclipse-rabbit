package rabbit.ui.tests;

import rabbit.ui.internal.SharedImages;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class SharedImagesTest {

	@Test
	public void expandAllImage() {
		assertNotNull(SharedImages.EXPAND_ALL);
	}
	
	@Test
	public void refreshImage() {
		assertNotNull(SharedImages.REFRESH);
	}
}
