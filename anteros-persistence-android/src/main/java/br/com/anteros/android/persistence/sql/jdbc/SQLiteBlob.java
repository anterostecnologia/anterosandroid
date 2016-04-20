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

package br.com.anteros.android.persistence.sql.jdbc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;

/**
 * 
 * @author Edson Martins - Anteros
 *
 */

public class SQLiteBlob implements Blob {

	private InputStream stream;

	private int length;

	private byte bytes[];

	boolean freed = false;

	/**
	 * Construtor aceita qualquer objeto que possa ser serializado
	 * 
	 * @param obj
	 *            Objeto
	 * @throws IOException
	 */
	public SQLiteBlob(Object obj) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(obj);
		bytes = baos.toByteArray();
		this.stream = new ByteArrayInputStream(bytes);
		this.length = bytes.length;
	}

	/**
	 * Construtor aceita array de bytes
	 * 
	 * @param bytes
	 *            Array de bytes
	 */
	public SQLiteBlob(byte[] bytes) {
		this.bytes = bytes;
		this.stream = new ByteArrayInputStream(bytes);
		this.length = bytes.length;
	}

	/**
	 * Construtor aceita InputStream
	 * 
	 * @param stream
	 *            InputStream
	 */
	public SQLiteBlob(InputStream stream) {
		this.stream = stream;
	}

	public Object getObject() throws Exception {
		checkFreed();

		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois;
		ois = new ObjectInputStream(bais);
		return ois.readObject();
	}

	public long length() throws SQLException {
		checkFreed();

		return length;
	}

	public byte[] getBytes(long pos, int length) throws SQLException {
		checkFreed();

		byte bytesToReturn[] = new byte[length];
		for (int i = 0; i < length; i++)
			bytesToReturn[i] = bytes[(int) pos + i];

		return bytesToReturn;
	}

	public int setBytes(long value, byte[] bytes, int pos, int length) throws SQLException {
		checkFreed();

		return -1;
	}

	public void truncate(long value) throws SQLException {
		checkFreed();
	}

	public int setBytes(long value, byte[] bytes) throws SQLException {
		checkFreed();
		return -1;
	}

	public InputStream getBinaryStream() throws SQLException {
		checkFreed();

		return stream;
	}

	/**
	 * Returna um InputStream contendo parte do valor do Blob, iniciando da
	 * posição inicial até o tamanho especificado.
	 * 
	 * @param pos
	 *            Posição inicial
	 * @param length
	 *            Tamanho
	 */
	public InputStream getBinaryStream(long pos, long length) throws SQLException {
		checkFreed();

		return new ByteArrayInputStream(getBytes(pos, (int) length));
	}

	public OutputStream setBinaryStream(long value) throws SQLException {
		checkFreed();

		return null;
	}

	public void free() throws SQLException {
		if (freed) {
			return;
		}

		bytes = null;
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException ioe) {
			}
		}
		freed = true;
	}

	public long position(byte[] pattern, long start) throws SQLException {
		checkFreed();

		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	public long position(Blob pattern, long start) throws SQLException {
		checkFreed();

		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	protected void checkFreed() throws SQLException {
		if (freed)
			throw new SQLException("Objeto já foi destruído(método free foi chamado).");
	}
}