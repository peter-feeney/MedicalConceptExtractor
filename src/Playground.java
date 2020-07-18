package metamap_experiments;
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
	 *	-k --exclude_sts <semtypelist>    : exclude semantic types
	 *	-l --allow_large_n             : allow Large N
	 *	-o --allow_overmatches : allow overmatches 
	 *	-r --threshold <integer>          : Threshold for displaying candidates. 
	 *	-y --word_sense_disambiguation : use WSD 
	 *	-z --term_processing : use term processing
 */




public class Playground {

	public static void main(String[] args) throws Exception {
		/*
		 * Create list of options and feed into custom API:
		 * ArrayList<String> optionList = new ArrayList<String>();
		 * optionList.add("-C");
		 */
		myAPI myapi = new myAPI();
		MetaMapApi api = myapi.getApi();
		System.out.println(myapi.optionsString()); //should be blank
		
		//Create spreadsheet
		Spreadsheet captions = new Spreadsheet();
		//index of captions is 2
		int capIndex = 2;
		System.out.println(captions.test());
		int rows = captions.getRows();
		int columns = captions.getColumns();
		
		for(int row = 0; row < rows; row++) {
			String caption = captions.readCap(row, capIndex);
			String annotation = process(caption);
			captions.write(annotation);
		}
		
		boolean success = captions.makeCSV();
		if(success) {
			System.out.println("Successfully created CSV file");
		} else {
			System.out.println("Failed to create CSV file");
		}
		
	}
	
	/**
	 * Generate processed output from captions
	 * @param the caption/question
	 * @return list of CUIs, preferred concept name, etc
	 * @throws Exception 
	 */
	
	private static String process(String input) throws Exception {
		
		myAPI myapi = new myAPI();
		MetaMapApi api = myapi.getApi();
		System.out.println(myapi.optionsString()); //should be blank
		

		
		String testString1 = "Ventricle remains dilated with decreased size of intraventricular hemorrhage.";
		String testString2 = "Fluid collection present around left side of liver and around left upper quadrant.  Liver appears homogeneous.";
		String testString3 = "In what ventricle is the enhancing intraventricular mass seen?";
		
		
		List<Result> resultList = myapi.getApi().processCitationsFromString
				(testString3);
		Result result = resultList.get(0);
		List<AcronymsAbbrevs> aaList = result.getAcronymsAbbrevs();
		
		
		if (aaList.size() > 0) {
		  System.out.println("Acronyms and Abbreviations:");
		  for (AcronymsAbbrevs e: aaList) {
		    System.out.println("Acronym: " + e.getAcronym());
		    System.out.println("Expansion: " + e.getExpansion());
		    System.out.println("Count list: " + e.getCountList());
		    System.out.println("CUI list: " + e.getCUIList());
		  }
		} else {
		  System.out.println(" There are no acronymns in this caption.");
		}
		
		
		
		List<Negation> negList = result.getNegations();
		if (negList.size() > 0) {
		  System.out.println("Negations:");
		  for (Negation e: negList) {
		    System.out.println("type: " + e.getType());
		    System.out.print("Trigger: " + e.getTrigger() + ": [");
		    for (Position pos: e.getTriggerPositionList()) {
		      System.out.print(pos  + ",");
		    }
		    System.out.println("]");
		    System.out.print("ConceptPairs: [");
		    for (ConceptPair pair: e.getConceptPairList()) {
		      System.out.print(pair + ",");
		    }
		    System.out.println("]");
		    System.out.print("ConceptPositionList: [");
		    for (Position pos: e.getConceptPositionList()) {
		      System.out.print(pos + ",");
		    }
		    System.out.println("]");
		  }
		} else {
			System.out.println(" There are no negations in this caption.");
		}
		
		for (Utterance utterance: result.getUtteranceList()) {
			System.out.println("Utterance:");
			System.out.println(" Id: " + utterance.getId());
			System.out.println(" Utterance text: " + utterance.getString());
			System.out.println(" Position: " + utterance.getPosition());
			for (PCM pcm: utterance.getPCMList()) {
				System.out.println("Phrase:");
				System.out.println(" text: " + pcm.getPhrase().getPhraseText());
				System.out.println("Candidates:");
				for (Ev ev: pcm.getCandidateList()) {
		            System.out.println(" Candidate:");
		            System.out.println("  Score: " + ev.getScore());
		            System.out.println("  Concept Id: " + ev.getConceptId());
		            System.out.println("  Concept Name: " + ev.getConceptName());
		            System.out.println("  Preferred Name: " + ev.getPreferredName());
		            System.out.println("  Matched Words: " + ev.getMatchedWords());
		            System.out.println("  Semantic Types: " + ev.getSemanticTypes());
		            System.out.println("  MatchMap: " + ev.getMatchMap());
		            System.out.println("  MatchMap alt. repr.: " + ev.getMatchMapList());
		            System.out.println("  is Head?: " + ev.isHead());
		            System.out.println("  is Overmatch?: " + ev.isOvermatch());
		            System.out.println("  Sources: " + ev.getSources());
		            System.out.println("  Positional Info: " + ev.getPositionalInfo());
		            System.out.println("***************");
		            System.out.println("Done with this set of evs");
				}
				System.out.println("Mappings:");
				for (Mapping map: pcm.getMappingList()) {
					//System.out.println(" Map Score: " + map.getScore());
		            for (Ev mapEv: map.getEvList()) {
		            //System.out.println("   Score: " + mapEv.getScore());
		             System.out.println("   Concept Id: " + mapEv.getConceptId());
		             System.out.println("   Concept Name: " + mapEv.getConceptName());
		             System.out.println("   Preferred Name: " + mapEv.getPreferredName());
		             System.out.println("   Matched Words: " + mapEv.getMatchedWords());
		             System.out.println("   Semantic Types: " + mapEv.getSemanticTypes());
		             System.out.println("Done with this set of mappings");
		             System.out.println("#####################################");
		            // System.out.println("   MatchMap: " + mapEv.getMatchMap());
		            // System.out.println("   MatchMap alt. repr.: " + mapEv.getMatchMapList());
		            // System.out.println("   is Head?: " + mapEv.isHead());
		            // System.out.println("   is Overmatch?: " + mapEv.isOvermatch());
		            // System.out.println("   Sources: " + mapEv.getSources());
		            // System.out.println("   Positional Info: " + mapEv.getPositionalInfo());
		            }
				}
		System.out.println("-----------------");
		System.out.println("New set of PCMs:");
	}
			System.out.println("_____________________________");
			System.out.println("New set of utterances: ");
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		return "hello";
	}
	

}
