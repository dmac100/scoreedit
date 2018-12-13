package score;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Test;

public class ModelTest {
	@Test
	public void saveLoadSaveRoundTrip() throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		new Model().save(outputStream);
		String save1 = new String(outputStream.toByteArray(), "UTF-8");
		
		Model model = new Model();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		model.load(inputStream);
		
		ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
		model.save(outputStream2);
		String save2 = new String(outputStream2.toByteArray(), "UTF-8");
		
		assertEquals(save1, save2);
	}
}