package fr.labri.harmony.analysis.ownership;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import fr.labri.harmony.core.model.Data;

@Entity
public class ContributionData implements Data {
	
	@Id @GeneratedValue
	private int id;

	// Persistence attributes
	private int elementKind;
	private int elementId;
	
	// Ownership metrics
	private double contribution;
	
	// Item attributes
	private int contributorId;
	private int componentId;
	
	
	public ContributionData() {
		super();
	}
	
	public int getContributor() {
		return contributorId;
	}

	public void setContributor(int contributor) {
		this.contributorId = contributor;
	}
	
	public int getComponentId() {
		return componentId;
	}
	
	public void setComponentId(int id) {
		componentId = id;
	}

	public double getContribution() {
		return contribution;
	}

	public void setContribution(double contribution) {
		this.contribution = contribution;
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
