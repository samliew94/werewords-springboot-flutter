/**
 * 
 *
 * @author Sam Liew 5 Jan 2023 9:51:43 PM
 */
package com.util;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Sam Liew 5 Jan 2023 9:51:43 PM
 *
 */
public final class File {

	public static List<String> convert(String filePath) throws Exception
	{
	    Class clazz = File.class;

		Path path2 = Paths.get(clazz.getClassLoader().getResource(filePath).toURI());
			         
	    Stream<String> lines = Files.lines(path2, StandardCharsets.ISO_8859_1);
	    List<String> collect = lines.collect(Collectors.toList());
	    lines.close();
		
		return collect;
	}
	
}

