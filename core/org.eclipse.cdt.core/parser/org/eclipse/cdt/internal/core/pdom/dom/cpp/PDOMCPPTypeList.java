/*******************************************************************************
 * Copyright (c) 2008, 2009 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Markus Schorn - initial API and implementation
 *******************************************************************************/ 
package org.eclipse.cdt.internal.core.pdom.dom.cpp;

import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.internal.core.pdom.db.Database;
import org.eclipse.cdt.internal.core.pdom.dom.PDOMLinkage;
import org.eclipse.cdt.internal.core.pdom.dom.PDOMNode;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;

/**
 * Stores a list of types
 */
class PDOMCPPTypeList {
	protected static final int NODE_SIZE = 4;

	/**
	 * Stores the given types in the database. 
	 * @return the record by which the types can be referenced.
	 */
	public static int putTypes(PDOMNode parent, IType[] types) throws CoreException {
		if (types == null)
			return 0;
		
		final PDOMLinkage linkage= parent.getLinkage();
		final Database db= linkage.getDB();
		final short len = (short)Math.min(types.length, (Database.MAX_MALLOC_SIZE-2)/NODE_SIZE); 
		final int block = db.malloc(2+ NODE_SIZE*len);
		int p = block;

		db.putShort(p, len); p+=2;
		for (int i=0; i<len; i++, p+=NODE_SIZE) {
			final IType type = types[i];
			int rec= 0;
			if (type != null) {
				final PDOMNode pdomType = linkage.addType(parent, type);
				if (pdomType != null) {
					rec= pdomType.getRecord();
				} 
			}
			db.putInt(p, rec);
		}
		return block;
	}
	
	public static IType[] getTypes(PDOMNode parent, int rec) throws CoreException {
		if (rec == 0)
			return null;
		
		final PDOMLinkage linkage= parent.getLinkage();
		final Database db= linkage.getDB();
		final short len = db.getShort(rec);
		
		if (len == 0)
			return IType.EMPTY_TYPE_ARRAY;
		
		Assert.isTrue(len >= 0 && len <= (Database.MAX_MALLOC_SIZE-2)/NODE_SIZE);
		rec+=2;
		IType[] result= new IType[len];
		for (int i=0; i<len; i++, rec+=NODE_SIZE) {
			final int typeRec= db.getInt(rec);
			if (typeRec != 0)
				result[i]= (IType)linkage.getNode(typeRec);
		}
		return result;
	}
	
	/**
	 * Restores an array of template arguments from the database.
	 */
	public static void clearTypes(PDOMNode parent, final int record) throws CoreException {
		if (record == 0)
			return;
		
		final PDOMLinkage linkage= parent.getLinkage();
		final Database db= linkage.getDB();
		final short len= db.getShort(record);
		
		Assert.isTrue(len >= 0 && len <= (Database.MAX_MALLOC_SIZE-2)/NODE_SIZE);
		int p= record+2;
		for (int i=0; i<len; i++, p+=NODE_SIZE) {
			final int typeRec= db.getInt(p);
			final IType t= (IType) linkage.getNode(typeRec);
			linkage.deleteType(t, parent.getRecord());
		}
		db.free(record);
	}
}
