package com.flatironschool.javacs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import org.jsoup.select.Elements;

public class WikiPhilosophy {
	
	final static WikiFetcher wf = new WikiFetcher();
	
	/**
	 * Tests a conjecture about Wikipedia and Philosophy.
	 * 
	 * https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
	 * 
	 * 1. Clicking on the first non-parenthesized, non-italicized link
     * 2. Ignoring external links, links to the current page, or red links
     * 3. Stopping when reaching "Philosophy", a page with no links or a page
     *    that does not exist, or when a loop occurs
	 * 
	 * @param args
	 * @throws IOException
	 */




	public static void main(String[] args) throws IOException {

		String url = "https://en.wikipedia.org/wiki/Java_(programming_language)";
		List<String> urlList = new ArrayList<String>();
		
		String goal = "https://en.wikipedia.org/wiki/Philosophy";
		while (url.equals(goal) == false) {
			Elements paragraphs = wf.fetchWikipedia(url);
			boolean found = false;
			//check for self-reference
			urlList.add(url);
			int num_open_parens = 0;
			int num_close_parens = 0;
			for (Element firstPara : paragraphs) {
				Iterable<Node> iter = new WikiNodeIterable(firstPara);
				boolean valid_link = false;
				for (Node node: iter) {
					if (node instanceof TextNode) {
						TextNode tn = (TextNode) node;
						String parens = tn.text();
						num_open_parens += parens.length() - parens.replace("(", "").length();
							num_close_parens += parens.length() - parens.replace(")", "").length();

						} else {
							//is an element
							Element e = (Element) node;
							if (e.tagName() == "a") {
								found = true;
								//check for valid link
								boolean has_italics = false;
								Elements possible_italics = e.parents();
								for (Element italics : possible_italics) {
									if (italics.tagName().equals("i")) {
										has_italics = true;
									//System.out.println("italics is " + italics.toString());							
										break;
									}
								}
								//System.out.println("italics is " + italics.toString());							
								String next_page = node.attr("href");
								String next_page_url = "https://en.wikipedia.org" + next_page;
								int end = next_page_url.indexOf('#');
								boolean self_referential = false;
								if (end != -1) {
									self_referential = true;
								}
								if (urlList.contains(next_page_url)) {
								//throw an error
									self_referential = true;
								}
								if ((num_open_parens - num_close_parens)!=0 || (has_italics == true) || (self_referential == true)) {
									found = false;
								} else {
									url = next_page_url;
									break;
								}
							}
						}	
					}
					//within article
					if (found) break;
				}
				//finished article
				if (found == false) {
					System.out.println("Return false.");
					break;
				}

			}
			urlList.add(url);
			System.out.println(urlList.toString());
		}
	}
