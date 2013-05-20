package fr.labri.harmony.analysis.ownership;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import fr.labri.harmony.core.model.Data;

@Entity
public class ComponentData implements Data {
	
	@Id @GeneratedValue
	private int id;

	// Persistence attributes
	private int elementKind;
	private int elementId;
	
	// Ownership metrics
	private double ownership;
	private int majorContributor;
	private int minorContributor;
	
	// Item attributes
	private int ownerId;
	private int bugCount;
	private String name;
	
	
	public ComponentData() {
		super();
	}
	
	public int getBugCount() {
		return bugCount;
	}
	
	public void setBugCount(int count) {
		bugCount = count;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public int getOwner() {
		return ownerId;
	}

	public void setOwner(int owner) {
		this.ownerId = owner;
	}

	public double getOwnership() {
		return ownership;
	}

	public void setOwnership(double ownership) {
		this.ownership = ownership;
	}

	public int getMajorContributor() {
		return majorContributor;
	}

	public void setMajorContributor(int majorContributor) {
		this.majorContributor = majorContributor;
	}

	public int getMinorContributor() {
		return minorContributor;
	}

	public void setMinorContributor(int minorContributor) {
		this.minorContributor = minorContributor;
	}
	
	@Override
	public int getElementId() {
		return elementId;
	}

	@Override
	public void setElementId(int elementId) {
		this.elementId = elementId;
	}

	@Override
	public int getElementKind() {
		return elementKind;
	}

	@Override
	public void setElementKind(int elementKind) {
		this.elementKind = elementKind;
	}
}
