package rs.raf.classes;

import rs.raf.enums.AddOns;

import java.lang.reflect.Array;
import java.util.List;

public class Classroom {
    private String name;
    private int capacity;
    private List<AddOns> addOns;

    public Classroom(String name, int capacity, List<AddOns> addOns) {
        this.name = name;
        this.capacity = capacity;
        this.addOns = addOns;
    }

    @Override
    public boolean equals(Object obj) {

        //TODO odraditi ovaj equals da proveri gluposti, mozda izbaciti ime iz equals u Term??
        return super.equals(obj);
    }
}
