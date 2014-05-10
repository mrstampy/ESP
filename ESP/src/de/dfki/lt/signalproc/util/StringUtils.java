/*
 * ESP Copyright (C) 2013 - 2014 Burton Alexander
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 */
package de.dfki.lt.signalproc.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

// TODO: Auto-generated Javadoc
/**
 * The Class StringUtils.
 */
public class StringUtils {

	// Removes blanks in the beginning and at the end of a string
	/**
	 * Deblank.
	 *
	 * @param str the str
	 * @return the string
	 */
	public static String deblank(String str) {
		StringTokenizer s = new StringTokenizer(str, " ", false);
		String strRet = "";

		while (s.hasMoreElements())
			strRet += s.nextElement();

		return strRet;
	}

	// Converts a String to a float
	/**
	 * String2 float.
	 *
	 * @param str the str
	 * @return the float
	 */
	public static float String2Float(String str) {
		return Float.valueOf(str).floatValue();
	}

	// Converts a String to a double
	/**
	 * String2 double.
	 *
	 * @param str the str
	 * @return the double
	 */
	public static double String2Double(String str) {
		return Double.valueOf(str).doubleValue();
	}

	// Converts a String to an int
	/**
	 * String2 int.
	 *
	 * @param str the str
	 * @return the int
	 */
	public static int String2Int(String str) {
		return Integer.valueOf(str).intValue();
	}

	// Find indices of multiple occurrences of a character in a String
	/**
	 * Find.
	 *
	 * @param str the str
	 * @param ch the ch
	 * @param stInd the st ind
	 * @param enInd the en ind
	 * @return the int[]
	 */
	public static int[] find(String str, char ch, int stInd, int enInd) {
		int[] indices = null;
		int i;
		int count = 0;

		if (stInd < 0)
			stInd = 0;
		if (stInd > str.length() - 1)
			stInd = str.length() - 1;
		if (enInd < stInd)
			enInd = stInd;
		if (enInd > str.length() - 1)
			enInd = str.length() - 1;

		for (i = stInd; i <= enInd; i++) {
			if (str.charAt(i) == ch)
				count++;
		}

		if (count > 0)
			indices = new int[count];

		int total = 0;
		for (i = stInd; i <= enInd; i++) {
			if (str.charAt(i) == ch && total < count)
				indices[total++] = i;
		}

		return indices;
	}

	/**
	 * Find.
	 *
	 * @param str the str
	 * @param ch the ch
	 * @param stInd the st ind
	 * @return the int[]
	 */
	public static int[] find(String str, char ch, int stInd) {
		return find(str, ch, stInd, str.length() - 1);
	}

	/**
	 * Find.
	 *
	 * @param str the str
	 * @param ch the ch
	 * @return the int[]
	 */
	public static int[] find(String str, char ch) {
		return find(str, ch, 0, str.length() - 1);
	}

	// Check last folder separator character and append it if it does not exist
	/**
	 * Check last slash.
	 *
	 * @param strIn the str in
	 * @return the string
	 */
	public static String checkLastSlash(String strIn) {
		String strOut = strIn;

		char last = strIn.charAt(strIn.length() - 1);

		if (last != File.separatorChar)
			strOut = strOut + File.separatorChar;

		return strOut;
	}

	// Check first file extension separator character and add it if it does not
	// exist
	/**
	 * Check first dot.
	 *
	 * @param strIn the str in
	 * @return the string
	 */
	public static String checkFirstDot(String strIn) {
		String strOut = strIn;

		char extensionSeparator = '.';

		char first = strIn.charAt(0);

		if (first != extensionSeparator)
			strOut = extensionSeparator + strOut;

		return strOut;
	}

	// Default start index is 1
	/**
	 * Indexed name generator.
	 *
	 * @param preName the pre name
	 * @param numFiles the num files
	 * @return the string[]
	 */
	public static String[] indexedNameGenerator(String preName, int numFiles) {
		return indexedNameGenerator(preName, numFiles, 1);
	}

	/**
	 * Indexed name generator.
	 *
	 * @param preName the pre name
	 * @param numFiles the num files
	 * @param startIndex the start index
	 * @return the string[]
	 */
	public static String[] indexedNameGenerator(String preName, int numFiles, int startIndex) {
		return indexedNameGenerator(preName, numFiles, startIndex, "");
	}

	/**
	 * Indexed name generator.
	 *
	 * @param preName the pre name
	 * @param numFiles the num files
	 * @param startIndex the start index
	 * @param postName the post name
	 * @return the string[]
	 */
	public static String[] indexedNameGenerator(String preName, int numFiles, int startIndex, String postName) {
		return indexedNameGenerator(preName, numFiles, startIndex, postName, ".tmp");
	}

	/**
	 * Indexed name generator.
	 *
	 * @param preName the pre name
	 * @param numFiles the num files
	 * @param startIndex the start index
	 * @param postName the post name
	 * @param extension the extension
	 * @return the string[]
	 */
	public static String[] indexedNameGenerator(String preName, int numFiles, int startIndex, String postName,
			String extension) {
		int numDigits = 0;
		if (numFiles > 0)
			numDigits = (int) Math.floor(Math.log10(startIndex + numFiles - 1));

		return indexedNameGenerator(preName, numFiles, startIndex, postName, extension, numDigits);
	}

	// Generate a list of files in the format:
	// <preName>startIndex<postName>.extension
	// <preName>startIndex+1<postName>.extension
	// <preName>startIndex+2<postName>.extension
	// ...
	// The number of required characters for the largest index is computed
	// automatically if numDigits<required number of characters for the largest
	// index
	// The minimum value of startIndex is 0 (negative values are converted to
	// zero)
	/**
	 * Indexed name generator.
	 *
	 * @param preName the pre name
	 * @param numFiles the num files
	 * @param startIndex the start index
	 * @param postName the post name
	 * @param extension the extension
	 * @param numDigits the num digits
	 * @return the string[]
	 */
	public static String[] indexedNameGenerator(String preName, int numFiles, int startIndex, String postName,
			String extension, int numDigits) {
		String[] fileList = null;

		if (numFiles > 0) {
			if (startIndex < 0)
				startIndex = 0;

			int tmpDigits = (int) Math.floor(Math.log10(startIndex + numFiles - 1));
			if (tmpDigits > numDigits)
				numDigits = tmpDigits;

			fileList = new String[numFiles];

			String strNum;

			for (int i = startIndex; i < startIndex + numFiles; i++) {
				strNum = String.valueOf(i);

				// Add sufficient 0´s in the beginning
				while (strNum.length() < numDigits)
					strNum = "0" + strNum;
				//

				fileList[i - startIndex] = preName + strNum + postName + extension;
			}
		}

		return fileList;
	}

	/**
	 * Modify extension.
	 *
	 * @param strFilename the str filename
	 * @param desiredExtension the desired extension
	 * @return the string
	 */
	public static String modifyExtension(String strFilename, String desiredExtension) {
		String strNewname = strFilename;
		String desiredExtension2 = checkFirstDot(desiredExtension);

		int lastDotIndex = strNewname.lastIndexOf('.');
		strNewname = strNewname.substring(0, lastDotIndex) + desiredExtension2;

		return strNewname;
	}

	// This version assumes that there can only be insertions and deletions but no
	// substitutions
	// (i.e. text based alignment with possible differences in pauses only)
	/**
	 * Align labels.
	 *
	 * @param seq1 the seq1
	 * @param seq2 the seq2
	 * @return the int[][]
	 */
	public static int[][] alignLabels(ESTLabel[] seq1, ESTLabel[] seq2) {
		return alignLabels(seq1, seq2, 0.05, 0.05, 0.05);
	}

	/**
	 * Align labels.
	 *
	 * @param labs1 the labs1
	 * @param labs2 the labs2
	 * @param PDeletion the p deletion
	 * @param PInsertion the p insertion
	 * @param PSubstitution the p substitution
	 * @return the int[][]
	 */
	public static int[][] alignLabels(ESTLabel[] labs1, ESTLabel[] labs2, double PDeletion, double PInsertion,
			double PSubstitution) {
		double PCorrect = 1.0 - (PDeletion + PInsertion + PSubstitution);
		int n = labs1.length;
		int m = labs2.length;
		double D;
		int[][] labelMap = null;

		if (n == 0 || m == 0) {
			D = m;
			return labelMap;
		}

		int i, j;
		double[][] d = new double[n + 1][m + 1];
		for (i = 0; i < d.length; i++) {
			for (j = 0; j < d[i].length; j++)
				d[i][j] = 0.0;
		}

		int[][] p = new int[n + 1][m + 1];
		for (i = 0; i < p.length; i++) {
			for (j = 0; j < p[i].length; j++)
				p[i][j] = 0;
		}

		double z = 1;
		d[0][0] = z;
		for (i = 1; i <= n; i++)
			d[i][0] = d[i - 1][0] * PDeletion;

		for (j = 1; j <= m; j++)
			d[0][j] = d[0][j - 1] * PInsertion;

		String strEvents = "DISC";
		double c;
		double tmp;
		for (i = 1; i <= n; i++) {
			for (j = 1; j <= m; j++) {
				if (labs1[i - 1].phn.compareTo(labs2[j - 1].phn) == 0)
					c = PCorrect;
				else
					c = PSubstitution;

				int ind = 1;
				d[i][j] = d[i - 1][j] * PDeletion;
				tmp = d[i][j - 1] * PInsertion;
				if (tmp > d[i][j]) {
					d[i][j] = tmp;
					ind = 2;
				}

				tmp = d[i - 1][j - 1] * c;
				if (tmp > d[i][j]) {
					d[i][j] = tmp;
					ind = 3;
				}

				if (ind == 3 && labs1[i - 1].phn.compareTo(labs2[j - 1].phn) == 0)
					ind = 4;

				// Events 1:Deletion, 2:Insertion, 3:Substitution, 4:Correct
				p[i][j] = ind;
			}
		}

		// Backtracking
		D = d[n][m];
		int k = 1;
		int[] E = new int[m * n];
		E[k - 1] = p[n][m];
		i = n + 1;
		j = m + 1;
		int t = m;
		while (true) {
			if (E[k - 1] == 3 || E[k - 1] == 4) {
				i = i - 1;
				j = j - 1;
			} else if (E[k - 1] == 2)
				j = j - 1;
			else if (E[k - 1] == 1)
				i = i - 1;

			if (p[i - 1][j - 1] == 0) {
				while (j > 1) {
					k = k + 1;
					j = j - 1;
					E[k - 1] = 2;
				}
				break;
			} else {
				k = k + 1;
				E[k - 1] = p[i - 1][j - 1];
			}
			t = t - 1;
		}

		// Reverse the order
		int[] Events = new int[k];
		for (t = k; t >= 1; t--)
			Events[t - 1] = E[k - t];

		int[][] tmpLabelMap = new int[n * m][2];
		int ind = 0;
		int ind1 = 0;
		int ind2 = 0;
		for (t = 1; t <= k; t++) {
			if (Events[t - 1] == 3 || Events[t - 1] == 4) // Substitution or correct
			{
				tmpLabelMap[ind][0] = ind1;
				tmpLabelMap[ind][1] = ind2;
				ind1++;
				ind2++;
				ind++;
			} else if (Events[t - 1] == 1) // An item in seq1 is deleted in seq2
			{
				ind1++;
			} else if (Events[t - 1] == 2) // An item is inserted in seq2
			{
				ind2++;
			}
		}

		if (ind > 0) {
			labelMap = new int[ind][2];
			for (i = 0; i < labelMap.length; i++) {
				labelMap[i][0] = tmpLabelMap[i][0];
				labelMap[i][1] = tmpLabelMap[i][1];
			}
		}

		return labelMap;
	}

	/**
	 * Find in map.
	 *
	 * @param map the map
	 * @param ind1 the ind1
	 * @return the int
	 */
	public static int findInMap(int[][] map, int ind1) {
		for (int i = 0; i < map.length; i++) {
			if (map[i][0] == ind1)
				return map[i][1];
		}

		return -1;
	}

	/**
	 * Checks if is numeric.
	 *
	 * @param str the str
	 * @return true, if is numeric
	 */
	public static boolean isNumeric(String str) {
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (!Character.isDigit(ch) && ch != '.')
				return false;
		}

		return true;
	}

	// Retrieves filename from fullpathname
	// Also works for removing file extension from a filename with extension
	/**
	 * Gets the file name.
	 *
	 * @param fullpathFilename the fullpath filename
	 * @param bRemoveExtension the b remove extension
	 * @return the file name
	 */
	public static String getFileName(String fullpathFilename, boolean bRemoveExtension) {
		String filename = "";

		int ind1 = fullpathFilename.lastIndexOf('\\');
		int ind2 = fullpathFilename.lastIndexOf('/');

		ind1 = Math.max(ind1, ind2);

		if (ind1 >= 0 && ind1 < fullpathFilename.length() - 2)
			filename = fullpathFilename.substring(ind1 + 1);

		if (bRemoveExtension) {
			ind1 = filename.lastIndexOf('.');
			if (ind1 > 0 && ind1 - 1 >= 0)
				filename = filename.substring(0, ind1);
		}

		return filename;
	}

	/**
	 * Gets the file name.
	 *
	 * @param fullpathFilename the fullpath filename
	 * @return the file name
	 */
	public static String getFileName(String fullpathFilename) {
		return getFileName(fullpathFilename, true);
	}

	/**
	 * Gets the folder name.
	 *
	 * @param fullpathFilename the fullpath filename
	 * @return the folder name
	 */
	public static String getFolderName(String fullpathFilename) {
		String foldername = "";

		int ind1 = fullpathFilename.lastIndexOf('\\');
		int ind2 = fullpathFilename.lastIndexOf('/');

		ind1 = Math.max(ind1, ind2);

		if (ind1 >= 0 && ind1 < fullpathFilename.length() - 2)
			foldername = fullpathFilename.substring(0, ind1 + 1);

		return foldername;
	}

	/**
	 * Parses the from lines.
	 *
	 * @param lines the lines
	 * @param minimumItemsInOneLine the minimum items in one line
	 * @param startLine the start line
	 * @param endLine the end line
	 * @return the string[][]
	 */
	public static String[][] parseFromLines(String[] lines, int minimumItemsInOneLine, int startLine, int endLine) {
		String[][] labels = null;
		String[][] labelsRet = null;

		if (startLine <= endLine) {
			int i, j;
			int count = 0;
			for (i = startLine; i <= endLine; i++) {
				String[] labelInfos = null;
				if (minimumItemsInOneLine > 1) {
					labelInfos = lines[i].split(" ");
				} else {
					labelInfos = new String[1];
					labelInfos[0] = lines[i];
				}

				boolean isNotEmpty = false;
				for (j = 0; j < labelInfos.length; j++) {
					labelInfos[j] = labelInfos[j].trim();
					if (labelInfos[j].length() != 0)
						isNotEmpty = true;
				}

				if (labelInfos.length > 0 && isNotEmpty)
					count++;
			}

			int tmpCount = 0;
			if (count > 0) {
				labels = new String[count][];
				for (i = startLine; i <= endLine; i++) {
					if (tmpCount > count - 1)
						break;

					String[] labelInfos = null;
					if (minimumItemsInOneLine > 1) {
						labelInfos = lines[i].split(" ");
					} else {
						labelInfos = new String[1];
						labelInfos[0] = lines[i];
					}

					boolean isNotEmpty = false;
					for (j = 0; j < labelInfos.length; j++) {
						labelInfos[j] = labelInfos[j].trim();
						if (labelInfos[j].length() != 0)
							isNotEmpty = true;
					}

					if (labelInfos.length > 0 && isNotEmpty) {
						labels[tmpCount] = new String[minimumItemsInOneLine];
						for (j = 0; j < Math.min(labelInfos.length, minimumItemsInOneLine); j++)
							labels[tmpCount][j] = labelInfos[j].trim();

						tmpCount++;
					}
				}

				labelsRet = new String[tmpCount][];
				for (i = 0; i < tmpCount; i++) {
					labelsRet[i] = new String[minimumItemsInOneLine];
					for (j = 0; j < minimumItemsInOneLine; j++)
						labelsRet[i][j] = labels[i][j];
				}
			}
		}

		return labelsRet;
	}

	/**
	 * Gets the different items list.
	 *
	 * @param items the items
	 * @return the different items list
	 */
	public static int[] getDifferentItemsList(int[] items) {
		int[] differentItems = null;
		int[] indices = getDifferentItemsIndices(items);

		if (indices != null) {
			differentItems = new int[indices.length];
			for (int i = 0; i < indices.length; i++)
				differentItems[i] = items[indices[i]];
		}

		return differentItems;
	}

	/**
	 * Gets the different items indices.
	 *
	 * @param items the items
	 * @return the different items indices
	 */
	public static int[] getDifferentItemsIndices(int[] items) {
		String[] strItems = new String[items.length];

		for (int i = 0; i < items.length; i++)
			strItems[i] = String.valueOf(items[i]);

		return getDifferentItemsIndices(strItems);
	}

	/**
	 * Gets the different items list.
	 *
	 * @param items the items
	 * @return the different items list
	 */
	public static String[] getDifferentItemsList(String[] items) {
		String[] differentItems = null;
		int[] indices = getDifferentItemsIndices(items);

		if (indices != null) {
			differentItems = new String[indices.length];
			for (int i = 0; i < indices.length; i++)
				differentItems[i] = items[indices[i]];
		}

		return differentItems;
	}

	/**
	 * Gets the different items indices.
	 *
	 * @param items the items
	 * @return the different items indices
	 */
	public static int[] getDifferentItemsIndices(String[] items) {
		int[] differentItemIndices = null;

		if (items != null) {
			int[] tmpDifferentItemIndices = new int[items.length];
			int differentCount = 1;
			int i, j;
			tmpDifferentItemIndices[0] = 0;
			boolean bDifferent;
			for (i = 1; i < items.length; i++) {
				bDifferent = true;
				for (j = 0; j < differentCount; j++) {
					if (items[i].compareTo(items[tmpDifferentItemIndices[j]]) == 0) {
						bDifferent = false;
						break;
					}
				}

				if (bDifferent) {
					tmpDifferentItemIndices[differentCount] = i;
					differentCount++;

					if (differentCount >= items.length)
						break;
				}
			}

			differentItemIndices = new int[differentCount];
			System.arraycopy(tmpDifferentItemIndices, 0, differentItemIndices, 0, differentCount);
		}

		return differentItemIndices;
	}

	/**
	 * Checks if is desired.
	 *
	 * @param currentFeature the current feature
	 * @param desiredFeatures the desired features
	 * @return true, if is desired
	 */
	public static boolean isDesired(int currentFeature, int desiredFeatures) {
		return isDesired(currentFeature, desiredFeatures, 0);
	}

	/**
	 * Checks if is desired.
	 *
	 * @param currentFeature the current feature
	 * @param desiredFeatures the desired features
	 * @param maxFeatureStringLen the max feature string len
	 * @return true, if is desired
	 */
	public static boolean isDesired(int currentFeature, int desiredFeatures, int maxFeatureStringLen) {
		boolean bRet;

		String str1 = Integer.toBinaryString(desiredFeatures);
		String str2 = Integer.toBinaryString(currentFeature);

		if (maxFeatureStringLen < str1.length())
			maxFeatureStringLen = str1.length();
		if (maxFeatureStringLen < str2.length())
			maxFeatureStringLen = str2.length();

		while (str1.length() < maxFeatureStringLen)
			str1 = "0" + str1;

		while (str2.length() < maxFeatureStringLen)
			str2 = "0" + str2;

		bRet = true;
		for (int i = 0; i < str1.length(); i++) {
			if (Integer.valueOf(String.valueOf(str1.charAt(i))) == 1 && Integer.valueOf(String.valueOf(str2.charAt(i))) == 0)
				bRet = false;
		}

		return bRet;
	}

	/**
	 * Write text file.
	 *
	 * @param textInRows the text in rows
	 * @param textFile the text file
	 */
	public static void writeTextFile(String[] textInRows, String textFile) {
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter(textFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (out != null) {
			for (int i = 0; i < textInRows.length; i++)
				out.println(textInRows[i]);

			out.close();
		} else
			System.out.println("Error! Cannot create file: " + textFile);
	}

}
