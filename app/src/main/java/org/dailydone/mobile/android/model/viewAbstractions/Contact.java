package org.dailydone.mobile.android.model.viewAbstractions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class Contact {
    private String id;
    @Setter
    private String displayName;
    private List<String> telephoneNumbers;
    private List<String> mailAddresses;

    public Contact(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
        this.telephoneNumbers = new ArrayList<>();
        this.mailAddresses = new ArrayList<>();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Contact otherContact = (Contact) obj;

        return id != null && id.equals(otherContact.id);
    }

    public boolean contentsEqual(Contact otherContact) {
        if (this == otherContact) {
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
