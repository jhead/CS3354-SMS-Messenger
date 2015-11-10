package cs3354group10.messenger;

import java.util.ArrayList;

public class Contact {

    protected String name;
    public static ArrayList<Contact> contactList = new ArrayList<>();


    // TODO: additional contact information
    public Contact(String name) {
        this.name = name;
        contactList.add(this);
    }

    public String getName() {
        return this.name;
    }


}
