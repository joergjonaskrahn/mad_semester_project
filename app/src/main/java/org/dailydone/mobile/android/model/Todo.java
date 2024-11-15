package org.dailydone.mobile.android.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;

@Entity
@AllArgsConstructor
public class Todo implements Serializable {
    private static final long serialVersionUID = -6410064189686738560L;

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;
    private String description;

    private long expiry;

    private boolean done;

    private boolean favourite;

/*	// Associated contacts
	private List<String> contacts;*/

    public Todo() {
    }

    public Todo(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Todo(String name, String description, long expiry, boolean done, boolean favourite) {
        this.name = name;
        this.description = description;
        this.expiry = expiry;
        this.done = done;
        this.favourite = favourite;
    }

    /*	public List<String> getContacts() {
		return contacts;
	}

	public void setContacts(List<String> contacts) {
		this.contacts = contacts;
	}*/

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getExpiry() {
        return expiry;
    }

    public void setExpiry(long expiry) {
        this.expiry = expiry;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

	/*public boolean equals(Object other) {
		return this.getId() == ((Todo) other).getId();
	}

	public String toString() {
		return "{Todo " + this.id + " " + this.name + ", " + this.description + this.expiry
				+ "}";
	}*/

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Todo other = (Todo) obj;
        return expiry == other.expiry &&
                done == other.done &&
                favourite == other.favourite &&
                (name != null ? name.equals(other.name) : other.name == null) &&
                (description != null ? description.equals(other.description) : other.description == null);
    }

}
