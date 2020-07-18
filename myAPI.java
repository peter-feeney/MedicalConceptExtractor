package metamap_experiments;
import java.util.*;

import javax.naming.NameNotFoundException;

import gov.nih.nlm.nls.metamap.*;

/*
 * Wrapper class that makes it easier to deal with MetaMap apis,
 * and add and remove options to the server as the user sees fit.
 */

public class myAPI {

	private MetaMapApi api; 
	private List<String> myOptions;
	
	
	
	
	/*
	 * Constructor for non-custom API.
	 */
	
	public myAPI() {
		this.api = new MetaMapApiImpl();
		this.myOptions = new ArrayList<String>();
	}
	
	/**
	 * Constructor for custom API.
	 */
	
	public myAPI(List<String> myOptions) {
		this.api = new MetaMapApiImpl();
		this.myOptions = myOptions;
		this.api.setOptions(myOptions);
	}
	
	
	/**
	 * Add options to MetaMap api object.
	 * @param option
	 * @return new list of options
	 */
	
	public List<String> addOption(String option) {
		myOptions.add(option);
		api.resetOptions();
		api.setOptions(myOptions);
		return myOptions;
	}
	
	/** 
	 * Removes options from custom MetaMap api object.
	 * @param option to be removed.
	 * @return new list of options.
	 */
	
	public List<String> removeOption(String option) throws NameNotFoundException {
		if(!myOptions.contains(option)) {
			throw new NameNotFoundException();
		}
		myOptions.remove(option);
		api.resetOptions();
		api.setOptions(myOptions);
		return myOptions;
	}
	
	/**
	 * Getter method for list of selected options.
	 * @return list of selected options.
	 */
	
	public List<String> getOptions() {
		return myOptions;
	}
	
	/**
	 * Returns selected options in easy-to-read string form.
	 * @return selected options in easy-to-read string form.  
	 */
	
	public String optionsString() {
		if(myOptions.isEmpty()) {
			return "You have not yet customized this server.";
		}
		
		return "You've selected the following options for this server: " + Arrays.toString(myOptions.toArray());
	}
	
	
	public MetaMapApi getApi() {
		return this.api;
	}
	
	
	
}
