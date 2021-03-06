/*
 * Copyright 2016 Anteros Tecnologia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.anteros.android.core.communication.ftp;

/**
 * Implement this interface to build a new LIST parser. List parsers are called
 * to parse the result of a FTP LIST command send to the server in the list()
 * method. You can add a custom parser to your instance of FTPClient calling on
 * it the method addListParser.
 * 
 * @author Carlo Pelliccia
 * @see FTPClient#addListParser(FTPListParser)
 */
public interface FTPListParser {

	/**
	 * Parses a LIST command response and builds an array of FTPFile objects.
	 * 
	 * @param lines
	 *            The response to parse, splitted by line.
	 * @return An array of FTPFile objects representing the result of the
	 *         operation.
	 * @throws FTPListParseException
	 *             If this parser cannot parse the given response.
	 */
	public FTPFile[] parse(String[] lines) throws FTPListParseException;

}
