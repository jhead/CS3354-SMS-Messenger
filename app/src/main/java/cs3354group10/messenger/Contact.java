package cs3354group10.messenger;


import java.util.ArrayList;

public class Contact {

    public static ArrayList<Contact> contactList = new ArrayList<>();

    protected String name;

    // TODO: additional contact information
    public Contact(String name) {
        this.name = name;
        contactList.add(this);
    }

    public String getName() {
        return this.name;
    }


}
