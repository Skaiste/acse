package com.khubla.antlr4example;

import java.util.ArrayList;
import java.util.Collections;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;

public class SyntaxTree {
	ArrayList<SyntaxTree> nodes;
	ArrayList<String> tags;
	String nodeName;
	String value;
	int childNodeHash;

    public String getValue() {
        return value;
    }

    public String getNodeName() {
        return nodeName;
    }

    public ArrayList<SyntaxTree> getNodes() {
        return nodes;
    }

    public SyntaxTree(ArrayList<SyntaxTree> nodes, ArrayList<String> tags, String nodeName, String value, int hash) {
        this.nodes = nodes;
        this.tags = tags;
        this.nodeName = nodeName;
        this.value = value;
        this.childNodeHash = hash;
    }

    public SyntaxTree(ParseTree tree) {
		while (tree.getChildCount() == 1) {
			tree = tree.getChild(0);
		}
		if (tree.getChildCount() == 0) {
			nodeName = getClass(tree.getParent());
			value = tree.getText();
		}
		else {
			nodeName = getClass(tree);
			nodes = new ArrayList<SyntaxTree>();
			for (int i = 0; i < tree.getChildCount(); i++) {
				nodes.add(new SyntaxTree(tree.getChild(i)));
			}
		}
        generateTags();
		childNodeHash = generateNodeNameHash();
	}

	private void generateTags() {
		tags = new ArrayList<String>();
		tags.add(nodeName);
		if (nodes == null) return;
		for (SyntaxTree st : nodes) {
			tags.add(st.nodeName);
			for (String nodeTag : st.tags) {
				if (!tags.contains(nodeTag))
					tags.add(nodeTag);
			}
		}
	}

	public String toString() {
		String str = nodeName;
		if (nodes == null)
			str += "\n" + value;
		else
			for (SyntaxTree n : nodes) {
				str += "\n" + n.toString(1);
			}
		return str;
	}

	public String toString(int tabs) {
		String str = String.join("", Collections.nCopies(tabs, "\t")) + nodeName;
		if (nodes == null)
			str += " : " + value;
		else
			for (SyntaxTree n : nodes) {
				str += "\n" + n.toString(tabs+1);
			}
		return str;
	}

	public String toJSONstring() {
		String str = "{\n\t\"name\": \"" + nodeName + "\",\n\t";
		if (nodes == null)
			str += "\"value\": \"" + singleQuotesOnly(value) + "\"";
		else {
			str += "\"nodes\": [";
			for (SyntaxTree n : nodes) {
				str += "\n" + n.toJSONstring(2) + ",";
			}
			str = removeLastChar(str);
			str += "\n\t]";
		}
		str += "\n}";
		return str;
	}
	public String toJSONstring(int tabs) {
		String str = tabs(tabs) + "{\n " + tabs(tabs+1) + "\"name\": \"" + nodeName + "\",\n" + tabs(tabs+1);
		if (nodes == null)
			str += "\"value\": \"" + singleQuotesOnly(value) + "\"";
		else {
			str += "\"nodes\": [";
			for (SyntaxTree n : nodes) {
				str += "\n" + n.toJSONstring(tabs + 2) + ",";
			}
			str = removeLastChar(str);
			str += "\n" + tabs(tabs+1) + "]";
		}
		str += "\n" + tabs(tabs) + "}";
		return str;
	}

	public boolean compareWithOriginal(CharStream chs) {
        String spaceless = toSpacelessString();
        // make sure that all spaces are removed
        spaceless = spaceless.replaceAll("\\s+","");

        // get original string
        String original = chs.getText(new Interval(0, chs.size()));
        // remove comments if exist
        while (original.contains("/*")){
            int start = original.indexOf("/*");
            int end = original.indexOf("*/") + 2;
            original = original.substring(0, start) + original.substring(end);
        }
        while (original.contains("//")) {
            int start = original.indexOf("//");
            int end = original.indexOf("\n", start);
            original = original.substring(0, start) + original.substring(end);
        }
        // remove all spaces
        String origSpaceless = original.replaceAll("\\s+","");
        //System.out.println(origSpaceless);
        //System.out.println(spaceless);
        // return comparison
	    return origSpaceless.equals(spaceless);
    }

    private String toSpacelessString() {
	    if (nodes == null) {
	        if (value.equals("<EOF>"))
	            return "";
            return value;
        }
	    String str = "";
        for (SyntaxTree n : nodes) {
            str += n.toSpacelessString();
        }
	    return str;
    }

    private int generateNodeNameHash() {
    	if (nodes == null || nodes.size() == 0) return 0;
    	// check if the first tag should be deleted
        boolean firstTagShouldBeRemoved = true;
    	// check if first tag is one of the child node
        for (SyntaxTree st : nodes)
            if (st.getNodeName() == tags.get(0)) firstTagShouldBeRemoved = false;
        // remove the tag if it matches the node name
        if (firstTagShouldBeRemoved && tags.get(0) == nodeName){
            tags.remove(0);
        }

    	String childNodeNames = "";
    	for (String t : tags) {
    		childNodeNames += t;
		}
		return childNodeNames.hashCode();
	}

	private String tabs(int n) {
		return String.join("", Collections.nCopies(n, "\t"));
	}

	private String removeLastChar(String s) {
		return s.substring(0, s.length()-1);
	}

	private String singleQuotesOnly(String s) {
		return s.replace("\"","\\\"");
	}

	private String getClass(Object o) {
		return (o.getClass().toString().split("\\$")[1]).split("Context")[0];
	}

	public ArrayList<String> getTags() {
		return tags;
	}

    public int getChildNodeHash() {
        return childNodeHash;
    }
}