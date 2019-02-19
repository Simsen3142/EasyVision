package internationalization;

import java.util.Locale;
import java.util.ResourceBundle;

public class InternationalizationController {
// attributes	
	private static final int DEFAULT_LOCALE = 0;
	public static Locale currentLocale;
	// defines the locale objects 
	// each object corresponds to one ResourceBundle class
	public static final Locale[] supportedLocales = {
		    new Locale("en"),
		    new Locale("de")
	};
	
// methods
	public static void changeLocale(){
		ResourceBundle bundle = ResourceBundle.getBundle("ResourceBundle", currentLocale);
	}
}
