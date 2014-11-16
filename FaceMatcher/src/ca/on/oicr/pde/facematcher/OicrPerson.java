package ca.on.oicr.pde.facematcher;

/**
 * Container class for holding person's data
 */
public class OicrPerson {
	public String name = "";
	public String job = "";
	public String imageURL = "";
	public String sitsAt = "";
	public int imageID = 0; //raw id
	
	public boolean isValid () {
		return this.imageID != 0 && !this.name.isEmpty();
	}
	
	/**
	 * Setters and getters for name and image URL/id
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (null != name)
		    this.name = name;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		if (null != imageURL)
		    this.imageURL = imageURL;
	}

	public int getImageID() {
		return imageID;
	}

	public void setImageID(int imageID) {
		this.imageID = imageID;
	}
	
}
