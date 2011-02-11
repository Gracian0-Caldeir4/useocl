/*
 * USE - UML based specification environment
 * Copyright (C) 1999-2010 Mark Richters, University of Bremen
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

// $Id$

package org.tzi.use.uml.sys.soil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.tzi.use.uml.mm.MAssociationClass;
import org.tzi.use.uml.ocl.expr.ExpConstString;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.uml.sys.MLinkObject;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.util.StringUtil;
import org.tzi.use.util.soil.exceptions.evaluation.EvaluationFailedException;


/**
 * TODO
 * @author Daniel Gent
 *
 */
public class MNewLinkObjectStatement extends MStatement {
	/** TODO */
	private MAssociationClass fAssociationClass;
	/**
	 * List of the objects that participate in the link in the same order as association ends. 
	 */
	private List<MRValue> fParticipants;
	/**
	 * List of the qualifier values for the association ends. 
	 */
	private List<List<MRValue>> qualifier;
	/** TODO */
	private Expression fObjectName;
	/** TODO */
	private MLinkObject fCreatedLinkObject;
		
	/**
	 * TODO
	 * @param associationClass
	 * @param participants
	 * @param objectName
	 */
	public MNewLinkObjectStatement(
			MAssociationClass associationClass, 
			List<MRValue> participants, 
			Expression objectName) {
		
		fAssociationClass = associationClass;
		fParticipants = participants;
		fObjectName = objectName;
	}
	
	
	/**
	 * TODO
	 * @param associationClass
	 * @param participants
	 * @param objectName
	 */
	public MNewLinkObjectStatement(
			MAssociationClass associationClass, 
			List<MRValue> participants, 
			String objectName) {
		
		fAssociationClass = associationClass;
		fParticipants = participants;
		if (objectName != null) {
			fObjectName = new ExpConstString(objectName);
		}
	}
	
	
	/**
	 * TODO
	 * @return
	 */
	public MAssociationClass getAssociationClass() {
		return fAssociationClass;
	}
	
	
	/**
	 * TODO
	 * @return
	 */
	public List<MRValue> getParticipants() {
		return fParticipants;
	}
	
	
	/**
	 * TODO
	 * @return
	 */
	public Expression getObjectName() {
		return fObjectName;
	}
	
	
	/**
	 * TODO
	 * @return
	 */
	public MLinkObject getCreatedLinkObject() {
		return fCreatedLinkObject;
	}
	
	
	@Override
	protected void evaluate() throws EvaluationFailedException {
		List<List<Value>> qualifierValues = new ArrayList<List<Value>>();
		List<Value> empty = Collections.emptyList();
		
		if (qualifier != null) {
			for (List<MRValue> values : qualifier) {
				if (values == null) {
					qualifierValues.add(empty);
				} else {
					List<Value> thisQualifierValues = new ArrayList<Value>();
					for (MRValue v : values) {
						thisQualifierValues.add(evaluateRValue(v));
					}
					qualifierValues.add(thisQualifierValues);
				}
			}
		}
		
		// evaluate participants
		List<MObject> participants = 
			evaluateObjectRValues(fParticipants);
		
		String objectName;
		if (fObjectName == null) {
			objectName = fState.uniqueObjectNameForClass(fAssociationClass);
		} else {
			objectName = evaluateString(fObjectName);
		}
				
		// create link object
		fCreatedLinkObject = 
			createLinkObject(
					fAssociationClass, 
					objectName, 
					participants,
					qualifierValues);
	}


	@Override
	protected String shellCommand() {
		StringBuilder sb = new StringBuilder();
		sb.append("new ");
		sb.append(fAssociationClass.name());
		if (fObjectName != null) {
			sb.append("(");
			sb.append(fObjectName);
			sb.append(")");
		}
		sb.append(" between (");
		StringUtil.fmtSeq(sb, fParticipants, ",");
		sb.append(")");
		
		return sb.toString();
	}


	@Override
	public boolean hasSideEffects() {
		return true;
	}

	
	@Override
	public String toString() {
		return shellCommand();
	}
}
