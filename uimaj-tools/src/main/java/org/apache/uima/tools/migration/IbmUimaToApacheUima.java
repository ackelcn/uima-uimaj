/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.uima.tools.migration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.uima.internal.util.FileUtils;


/**
 * Migration utility for converting from IBM UIMA to Apache UIMA.
 * Updates package names and does various other string replacements.
 * Should be run on java code, descriptors, and other files that may have UIMA
 * package names in them (e.g., launch configurations, scripts).
 */
public class IbmUimaToApacheUima {
  private static Map packageMapping = new TreeMap();
  private static Map stringReplacements = new TreeMap();
  private static int MAX_FILE_SIZE = 1000000; //don't update files bigger than this
  private static Set extensions = new HashSet();
  
  /**
   * Main program.  Expects one argument, the name of a directory containing files to
   * update.  Subdirectories are processed recursively.   
   * @param args  Command line arguments  
   * @throws IOException if an I/O error occurs
   */
  public static void main(String[] args) throws IOException{
    //parse command line
    String dir = null;
    for (int i = 0; i < args.length; i++) {
      if (args[i].startsWith("-")) {
        if (args[i].equals("-ext")) {
          if (i + 1 >= args.length) {
            printUsageAndExit();
          }
          parseCommaSeparatedList(args[++i], extensions);
        }
        else {
          System.err.println("Unknown switch " + args[i]);
          printUsageAndExit();
        }
      }
      else {
        if (dir != null) {
          printUsageAndExit();
        }
        else {
          dir = args[i];
        }
      }
    }
    if (dir == null) {
      printUsageAndExit();
    }

    //read resource files
    //map from IBM UIMA package names to Apache UIMA package names
    readMapping("packageMapping.txt", packageMapping);
    //other string replacements
    readMapping("stringReplacements.txt", stringReplacements);

    //from system property, get list of file extensions to exclude
    
    //do the replacements
    replaceInAllFiles(new File(args[0]));
  }

  /**
   * Parses a comma separated list, entering each value into the results Collection.
   * Trailing empty strings are included in the results Collection.
   * @param string string to parse
   * @param results Collection to which each value will be added
   */
  private static void parseCommaSeparatedList(String string, Collection results) {
    String[] components = string.split(",",-1);
    for (int i = 0; i < components.length; i++) {
      results.add(components[i]);
    }    
  }

  /**
   * 
   */
  private static void printUsageAndExit() {
    System.err.println("Usage: java " + IbmUimaToApacheUima.class.getName() + " <directory> [-ext <fileExtensions>]");
    System.err.println("<fileExtensions> is a comma separated list of file extensions to process, e.g.: java,xml,properties");
    System.err.println("\tUse a trailing comma to include files with no extension (meaning their name contains no dot)");
    System.exit(1);
  }

  /**
   * Applies the necessary replacements to all files in the given directory.
   * Subdirectories are processed recursively.
   * 
   * @param dir diretory containing files to replace
   * @throws IOException if an I/O error occurs
   */
  private static void replaceInAllFiles(File dir) throws IOException {
    File[] fileList = dir.listFiles();
    for (int i = 0; i < fileList.length; i++) {
      File file = fileList[i];
      if (file.isFile()) {
        //skip files with extensions specified in the excludes list
        if (!extensions.isEmpty()) {
          String filename = file.getName();
          String ext="";
          int lastDot = filename.lastIndexOf('.');
          if (lastDot > -1) {
            ext = filename.substring(lastDot+1);
          }
          if (!extensions.contains(ext.toLowerCase())) {
            continue;
          }
        }
        
        //skip files that we can't read and write
        if (!file.canRead()) {
          System.err.println("Skipping unreadable file: " + file.getCanonicalPath());
          continue;
        }
        if (!file.canWrite()) {
          System.err.println("Skipping unwritable file: " + file.getCanonicalPath());
          continue;
        }
        //skip files that are too big
        if (file.length() > MAX_FILE_SIZE) {
          System.out.println("Skipping file " + file.getCanonicalPath() + " with size: " + file.length() + " bytes");
          continue;
        }
        
        //do the replacements
        replaceInFile(file);
      }
      
      //recursively call on subdirectories
      if (file.isDirectory()) {
        replaceInAllFiles(file);
      }
    }
  }
  

  /**
   * Applies replacements to a single file.
   * @param file the file to process
   */
  private static void replaceInFile(File file) throws IOException {
    //read file
    String original;
    try {
      original = FileUtils.file2String(file);
    }
    catch(IOException e) {
      System.err.println("Error reading " + file.getCanonicalPath());
      System.err.println(e.getMessage());
      return;
    }
    String contents = original;
    //loop over packages to replace
    //we do special processing for package names to try to handle the case where
    //user code exists in a package prefixed by com.ibm.uima.
    //We only replace the package name when it appears as part of a fully-qualified
    //class name in that package, not as a prefix of another package.
    Iterator entries = packageMapping.entrySet().iterator();
    while (entries.hasNext()) {
      Map.Entry entry = (Map.Entry)entries.next();
      String ibmPkg = (String)entry.getKey();
      String apachePkg = (String)entry.getValue();
      String regex = ibmPkg+"(\\.(\\*|[A-Z]\\w*))";
      contents = contents.replaceAll(regex, apachePkg + "$1");
    }
    //now apply simple string replacements
    entries = stringReplacements.entrySet().iterator();
    while (entries.hasNext()) {
      Map.Entry entry = (Map.Entry)entries.next();
      String src = (String)entry.getKey();
      String dest = (String)entry.getValue();
      //replace
      contents = contents.replaceAll(src, dest);      
    }    
    
    //write file if it changed
    if (!contents.equals(original)) {
      FileUtils.saveString2File(contents, file);
    }
  }
  /**
   * Reads the mapping from IBM UIMA package names to Apache UIMA
   * package names from a resource file and populates the packageMapping
   * field.
   */
  private static void readMapping(String fileName, Map map) throws IOException {
    URL pkgListFile = IbmUimaToApacheUima.class.getResource(fileName);
    InputStream inStream = pkgListFile.openStream();
    BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
    String line = reader.readLine();
    while (line != null) {
      String[] mapping = line.split(" ");
      map.put(mapping[0],mapping[1]);
      line = reader.readLine();
    }
    inStream.close();
  }
}
