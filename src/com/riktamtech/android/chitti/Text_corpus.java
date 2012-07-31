package com.riktamtech.android.chitti;

public class Text_corpus {

	public static String[] ok_responses = new String[]{"Very well!","That's fine","Fine","Cool","All right","Pretty fine","Alright"};
	// These ok_responses must be the replies if any of the above words appear in the input as well...
	public static String[] welcome_responses = new String[]{"Hey ! Nice to see you","Hey ! What's up?","Hello , how can I help you today ?","Hi there, nice to see you.. What's up?","Hi!"};
	
	public static String[] pleasant_gestures = new String[]{"Great!","Cool!","Nice!","Excellent!","Fantastic!","Super!"};
	// Should come when user gives pleasant responses...  or for positive understood reponses..
		// good, great, fine, i understand etc.
	
	public static String[] prasing_responses = new String[] {"Thanks !","That's so nice of you !","Thanks... always at your service!","Thanks, you are really a nice person!"};
	// Should come in case of prasing responses from the user..
	
	public static String[] corpus_really_good_phrases_response = new String[] {"Thanks !","That's so nice of you !","Thanks..always at your service!","Great manners, amazing stuff this!","Thanks, you are really a nice person!","Thank you boss, you are a pretty good person!"};
	
	public static String[] thanks_giving = new String[]{"Well, it's my pleasure !","My pleasure, that's what I'm there for ..!","You are indeed welcome!"};
	// Should come for all the inputs of Thanks || tnx || thnks....
	
	public static String[] hi_responses = new String[]{"Hi there, what's up?","Hi boss, something special? "};
	
	public static String[] yes_responses = new String[]{"Indeed!","Obviously!","Lucid it is!"};
	
	public static String[] corpus_good_words = {"awesome", "amazing", "brilliant", "dynamite","excellent", "fabulous", "fantastic","great", "roovy", "marvellous", "nice", "neat", "outstanding", "radical", "stupendous", "super", "swell", "terrific","wonderful"};

	public static String[] corpus_really_good_phrases = {"wow","way to go","super","you're special","outstanding","excellent","great ","good","neat","well done","remarkable","i knew you could do it","fantastic ","i'm proud of you","super star","nice work ","looking good","you're on top of it ","beautiful","now you're flying ","you're catching on","now you've got it ","you're incredible","bravo ","you're fantastic","hurray for you ","you're on target","you're on your way ","how nice","how smart","good job ","that's incredible","hot dog","dynamite ","you're beautiful","you're unique ","nothing can stop you now","good for you ","i like you","you're a winner ","remarkable job","beautiful work ","spectacular,you're darling ","you're precious","great discovery ","you've discovered the secret","bingo ","all right","you figured it out ","fantastic job","magnificent ","hip hip hooray","marvelous","terrific ","you're important","phenomenal ","you're sensational","super work ","creative job","super job ","excellent job","exceptional performance ","you're a real trooper ","you are responsible","you're exciting ","you learned it right ","what an imagination,what a good listener ","you're fun","you care ","you're growing up","you tried hard ","beautiful sharing ","outstanding performance","you're a good friend ","i trust you","you're important ","you mean a lot to me","you make me happy ","you belong","you've got a friend ","you make me laugh","you brighten my day ","i respect you","you mean the world to me ","that's correct","you're a joy ","you're a treasure","you're wonderful ","you're awesome","a+ job ","you're a-ok","my buddy","you make my day ","that's the best"};
	
	public static String[] bad_words = {"idiot","stupid","rascal","retard","dumb","rude"};

	// For most generalistic responses...
	
	public static String[] common_nouns = {"person","year","way","day","thing","man","world","life","hand","part","child","eye","woman","place","work","week","case","point","government","company","number","group","problem","fact"};
	public static String[] common_verbs = {"be","have","do","say","get","make","go","know","take","see","come","think","look","want","give","use","find","tell","ask","work","seem","feel","try","leave","call","search","find"};
	public static String[] common_adjectives = {"good","new","first","last","long","great","little","own","other","old","right","big","high","different","small","large","next","early","young","important","few","public","bad","same"};
	public static String[] common_prepositions = {"to","of","in","for","on","with","at","by","from","up","about","into","over","after","beneath","under"};
	public static String[] common_conjunctions = {"after","although","and","as","as far as",
			"as how","as if","as long as","as soon as","as though","as well as","because",
			"before","both","but","either","even if","even though","for","how","however",
			"if","if only","in case","in order that","neither","nor","now","once","only",
			"or","provided","rather than","since","so that","than","that","though","till",
	"unless","until","when","whenever","where","whereas","wherever","whether","while","yet"};
	public static String[] common_question_tags = {"what","when","where","who","how"};
	public static String[] common_helping_verbs = {"is","are","was","were","has","have","had","be","been","being"};
	
	public static String[] most_common_conjunctions = {"after","although","and","as","as far as",
		"as how","as if","as soon as","as well as","because","before","both","but","either",
		"even","for","how","ever","if","in case","in order that","neither","nor","now","once",
		"only","or","provided","rather than","since","so","that","than","though","till",
		"unless","until","when","whenever","where","whereas","wherever","whether","while",
		"yet"};
	
	public static String reg_exp_corpus_good_words()
	{
		String good_words_reg_exp="";
		for(int i = 0; i< corpus_good_words.length-1 ; i++)
		{
			good_words_reg_exp += ".*"+corpus_good_words[i]+".*|";
		}
		good_words_reg_exp += ".*"+corpus_good_words[corpus_good_words.length-1]+".*";
		return good_words_reg_exp;
	}
	
	public static String reg_exp_corpus_really_good_phrases()
	{
		String good_words_reg_exp="";
		for(int i = 0; i< corpus_really_good_phrases.length-1 ; i++)
		{
			good_words_reg_exp += ".*"+corpus_really_good_phrases[i]+".*|";
		}
		good_words_reg_exp += ".*"+corpus_really_good_phrases[corpus_really_good_phrases.length-1]+".*";
		return good_words_reg_exp;
	}
	public static String reg_exp_bad_words()
	{
		String bad_words_reg_exp = "";
		for (int i = 0; i< bad_words.length-1; i++)
		{
			bad_words_reg_exp += ".*"+bad_words[i]+".*|";
		}
		bad_words_reg_exp += ".*"+bad_words[bad_words.length-1] + ".*";
		return bad_words_reg_exp;
	}
	public static String reg_exp_common_nouns()
	{
		String common_nouns_reg_exp = "";
		for(int i=0;i<common_nouns.length -1;i++)
		{
			common_nouns_reg_exp += ".*"+common_nouns[i]+".*|";
		}
		
		common_nouns_reg_exp += ".*"+common_nouns[common_nouns.length -1]+".*";
		return common_nouns_reg_exp;
	}
	// This function is only for most general expressions...
	public static String reg_exp_nouns()
	{
		String common_nouns_reg_exp = "";
		for(int i=0;i<common_nouns.length -1;i++)
		{
			common_nouns_reg_exp += ""+common_nouns[i]+"|";
		}
		
		common_nouns_reg_exp += ""+common_nouns[common_nouns.length -1]+"";
		return common_nouns_reg_exp;
	}
	public static String reg_exp_common_adjectives()
	{
		String common_adj_reg_exp = "";
		for(int i=0;i<common_adjectives.length -1;i++)
		{
			common_adj_reg_exp += ".*"+common_adjectives[i]+".*|";
		}
		
		common_adj_reg_exp += ".*"+common_adjectives[common_adjectives.length -1]+".*";
		return common_adj_reg_exp;
	}
	public static String reg_exp_most_common_conjunctions()
	{
		String reg_exp = "";
		for(int i=0;i<most_common_conjunctions.length -1;i++)
		{
			reg_exp += " "+most_common_conjunctions[i]+" |"+most_common_conjunctions[i]+" | "+most_common_conjunctions[i]+"|";
		}
		
		reg_exp += " "+most_common_conjunctions[most_common_conjunctions.length -1]+" |"+most_common_conjunctions[most_common_conjunctions.length-1]+" | "+most_common_conjunctions[most_common_conjunctions.length-1]+"";
		return reg_exp;
	}
	public static String reg_exp_helping_verbs()
	{
		String reg_exp = "";
		for(int i=0;i<common_helping_verbs.length -1;i++)
		{
			reg_exp += " "+common_helping_verbs[i]+" |"+common_helping_verbs[i]+" | "+common_helping_verbs[i]+"|";
		}
		
		reg_exp += " "+common_helping_verbs[common_helping_verbs.length -1]+" |"+common_helping_verbs[common_helping_verbs.length-1]+" | "+common_helping_verbs[common_helping_verbs.length-1];
		return reg_exp;
	}
	
	public static String reg_exp_common_verbs()
	{
		String reg_exp = "";
		for(int i=0;i<common_verbs.length -1;i++)
		{
			reg_exp += ""+common_verbs[i]+"|";
		}
		
		reg_exp += ""+common_verbs[common_verbs.length -1]+"";
		return reg_exp;
	}
	public static String[] common_pronouns = {"i","we","you","he","she","they","it","me","mine","your","his","her","their","it's"};
//	public static String reg_exp_common_pronouns()
//	{
//		String reg_exp = "";
//		for(int i=0;i<common_pronouns.length -1;i++)
//		{
//			reg_exp += ".* "+common_prepositions[i]+" .*|"+common_pronouns[i]+" .*|.* "+common_pronouns[i]+"|";
//		}
//		
//		reg_exp += ".* "+common_pronouns[common_pronouns.length -1]+" .*|"+common_pronouns[common_pronouns.length-1]+" .*|.* "+common_pronouns[common_pronouns.length -1];
//		return reg_exp;
//	}
	
	public static String reg_exp_common_pronouns()
	{
		String reg_exp = "";
		for(int i=0;i<common_pronouns.length -1;i++)
		{
			reg_exp += ""+common_pronouns[i]+"|";
		}
		
		reg_exp += ""+common_pronouns[common_pronouns.length -1]+"";
		return reg_exp;
	}
}
