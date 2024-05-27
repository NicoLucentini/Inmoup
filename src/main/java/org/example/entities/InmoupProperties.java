package org.example.entities;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class InmoupProperties implements Serializable {
        public Map<String, InmoupProperty> propiedades = new HashMap<String, InmoupProperty>();
        public InmoupProperties(){}
    }
