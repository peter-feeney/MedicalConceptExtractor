package metamap_experiments;
import java.io.PrintStream;
import java.util.*;
import gov.nih.nlm.nls.metamap.*;


	/**
	 *  Here's a complete list of MetaMap options available in the API:
	 *  -@ --WSD <hostname>   : Which WSD server to use.
	 *	-8 --dynamic_variant_generation : dynamic variant generation
	 *	-A --strict_model               : use strict model 
	 *	-C --relaxed_model              : use relaxed model 
	 *	-D --all_derivational_variants  : all derivational variants
	 *	-J --restrict_to_sts <semtypelist>   : restrict to semantic types
	 *	-K --ignore_stop_phrases        : ignore stop phrases.
	 *	-R --restrict_to_sources <sourcelist> : restrict to sources
	 *	-S --tagger <sourcelist>          : Which tagger to use.
	 *	-V --mm_data_version <name>       : version of MetaMap data to use.
	 *	-X --truncate_candidates_mappings : truncate candidates mapping
	 *	-Y --prefer_multiple_concepts  : prefer multiple concepts
	 *	-Z --mm_data_year <name>          : year of MetaMap data to use.
	 *	-a --all_acros_abbrs           : allow Acronym/Abbreviation variants
	 *	-b --compute_all_mappings      : compute/display all mappings
	 *	-d --no_derivational_variants  : no derivational variants
	 *	-e --exclude_sources <sourcelist> : exclude semantic types
	 *	-g --allow_concept_gaps        : allow concept gaps
	 *	-i --ignore_word_order         : ignore word order
	 *	-k --exclude_sts <semtypelist>    : exclude semantic typesll
	 *	-l --allow_large_n             : allow Large N
	 *	-o --allow_overmatches : allow overmatches 
	 *	-r --threshold <integer>          : Threshold for displaying candidates. 
	 *	-y --word_sense_disambiguation : use WSD 
	 *	-z --term_processing : use term processing
	 */




public class Main {

	public static void main(String[] args) throws Exception {
		
		/*
		 * Create list of options and feed into custom API:
		 * ArrayList<String> optionList = new ArrayList<String>();
		 * optionList.add("-C");
		 */
		
		myAPI myapi = new myAPI();
		MetaMapApi api = myapi.getApi();
		System.out.println(myapi.optionsString()); //should be blank
		//String caption = "Fluid collection present around left side of liver and around left upper quadrant.  Liver appears homogeneous.";
		//System.out.println(process(caption, "", api, "synpic420"));
		
		//Create spreadsheet
		Spreadsheet captions = new Spreadsheet();
		//Spreadsheet VQARad = new Spreadsheet("/slurm_storage/feeneypa/VQA_RAD Dataset Public.xlsx"); //need to input VQA_RAD
		//index of captions is 2
		//index of annotations between 3 - 6 
		int modalityIndex = 3; //replace with index of questions
		int planeIndex = 4;
		int diagIndex = 5;
		int topicIndex = 6;
		
		
		System.out.println(captions.test()); //alter test method
		int rows = captions.getRows();
		int columns = captions.getColumns();
		System.out.println("Spreadsheet has " + rows + " rows");
		System.out.println("Spreadsheet has " + columns + " columns");
		
		for(int row = 0; row < rows - 1; row++) {
			System.out.print("On row: " + row);
			String modality = captions.readCap(row, modalityIndex);
			String plane = captions.readCap(row, planeIndex);
			String diag = captions.readCap(row, diagIndex);
			String topic = captions.readCap(row, topicIndex);
			String id = captions.getID(row);
			
			System.out.println(" image id " + id + " with modality: " + modality + " and plane: " + plane
					+ " and diagonosis: " + diag + " and topic " + topic);
			String modality_processed = process(modality, "", api, id);
			captions.write(modality_processed);
			String plane_processed = process(plane, "", api, id);
			captions.write(plane_processed);
			String diag_processed = process(diag, "", api, id);
			captions.write(diag_processed);
			String topic_processed = process(topic, "", api, id);
			captions.write(topic_processed);
		}
		
		boolean success = captions.writeTXT("annotations_output.txt"); //change so that old output isn't overrwritten
		if(success) {
			System.out.println("Successfully created TXT file");
		} else {
			System.out.println("Failed to create TXT file");
		}
		
		
	}
	
	/**
	 * Generate processed output from captions
	 * @param the caption/question
	 * @param options any options selected
	 * @param MetaMap api object to be worked with
	 * @return list of CUIs, preferred concept name, etc
	 * @throws Exception 
	 */
	
	public static String process(String term, String options, MetaMapApi api, String imageID) throws Exception {
		StringBuilder toReturn = new StringBuilder();
		//Set options of API
		if (options.trim().length() > 1) {
			api.setOptions(options);
			System.out.println("options: " + api.getOptions());
		}
		if (term.trim().length() > 0) {
			List<Result> resultList = api.processCitationsFromString(term);
			for (Result result : resultList) { 
				if (result != null) {
					/** write result as: cui|semtypes|concept-name|utterance */
					for (Utterance utterance : result.getUtteranceList()) { //iterate through sentences
						for (PCM pcm : utterance.getPCMList()) { 
							for (Mapping map : pcm.getMappingList()) {
								for (Ev mapEv : map.getEvList()) {
									StringBuilder sb = new StringBuilder();
									//sb.append(mapEv.getSemanticTypes().get(0));
									for (String semType : mapEv.getSemanticTypes()) { //.subList(0, mapEv.getSemanticTypes().size())
										sb.append(semType).append(",");
									}
									Set<String> sourceSet = new HashSet<String>();
									sourceSet.addAll(mapEv.getSources());
									//if (sourceSet.contains("ICD9CM") || sourceSet.contains("MTHICD9")) {
										toReturn.append(imageID + "|" + mapEv.getConceptId() + "|" + sb.toString() + "|"
												 + mapEv.getPreferredName() + "|"
												+ utterance.getString() +'\n');
									//}
								}
							}
						}
					}
				}
			}
		}
		return toReturn.toString();
	}
	

}
