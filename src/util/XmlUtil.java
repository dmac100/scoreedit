package util;

import org.jdom2.Element;

public class XmlUtil {
	public static Element addElement(Element parent, String name, Object value) {
		return addElement(parent, name).setText(String.valueOf(value));
	}
	
	public static Element addElement(Element parent, String name) {
		Element child = new Element(name);
		parent.addContent(child);
		return child;
	}
}
