package org.dailydone.mobile.android.model.viewAbstractions;

import java.util.HashSet;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Contact {
    private String id;
    private String displayName;
    private List<String> telephoneNumbers;
    private List<String> mailAddresses;

    @Override
    public boolean equals(Object obj) {
        // Check for reference equality
        if (this == obj) {
            return true;
        }

        // Check for null and class compatibility
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        // Cast the object to Contact and compare relevant fields
        Contact otherContact = (Contact) obj;

        // Compare IDs (assumes IDs are unique)
        return id != null && id.equals(otherContact.id);
    }

    public boolean contentsEqual(Contact otherContact) {
        if (this == otherContact) {
            System.out.println("!!! CONTENTS ARE THE SAME 0");
            System.out.println(getDisplayName());
            System.out.println(otherContact.displayName);
            return true;
        }

        if (otherContact == null || getClass() != otherContact.getClass()) {
            return false;
        }

        // Compare display names
        if (!displayName.equals(otherContact.displayName)) {
            return false;
        }

        // Compare telephone numbers
        if (telephoneNumbers == null) {
            if (otherContact.telephoneNumbers != null) {
                return false;
            }
        } else if (!new HashSet<>(telephoneNumbers).containsAll(otherContact.telephoneNumbers) ||
                !new HashSet<>(otherContact.telephoneNumbers).containsAll(telephoneNumbers)) {
            return false;
        }

        // Compare mail addresses
        if (mailAddresses == null) {
            if (otherContact.mailAddresses != null) {
                return false;
            }
        } else if (!new HashSet<>(mailAddresses).containsAll(otherContact.mailAddresses) ||
                !new HashSet<>(otherContact.mailAddresses).containsAll(mailAddresses)) {
            return false;
        }

        return true;
    }
}
