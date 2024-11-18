package org.dailydone.mobile.android.model.viewAbstractions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Contact {
    private int id;
    private String telephoneNumber;
    private String mailAddress;

    public boolean contentsEqual(Contact otherContact) {
        if (this == otherContact) {
            return true;
        }
        if (otherContact == null || getClass() != otherContact.getClass()) {
            return false;
        }
        return telephoneNumber.equals(otherContact.telephoneNumber) &&
                mailAddress.equals(otherContact.mailAddress);
    }
}
