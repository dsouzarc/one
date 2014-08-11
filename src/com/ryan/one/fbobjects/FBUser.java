package com.ryan.one.fbobjects;

public class FBUser {
    
    private final String name;
    private final long id;
    
    public FBUser(final String name, final long id) { 
        this.name = name;
        this.id = id;
    }
    
    public String getName() { 
        return this.name;
    }
    
    public long getID() { 
        return this.id;
    }

}
