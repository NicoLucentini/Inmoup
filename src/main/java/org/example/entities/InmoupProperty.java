package org.example.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

@Table(name = "Properties")
@Entity
public class InmoupProperty implements Serializable{

        public InmoupProperty(){}

        public void doUrl(){
            String inmobiliaria = (usuario_id + "-"+nombre).replaceAll(" ", "-");
            String tipoCasa = (tip_desc + "-en-" + con_desc + "-en-" +prp_dom).replaceAll(" ", "-");
            url = "inmoup.com.ar/" + inmobiliaria + "/inmuebles/" + propiedad_id + "/ficha/" + tipoCasa + "?btid=" + id ;
        }

        @Id
        @Column(name = "id")
        public Long id;
        public Integer propiedad_id;
        public String url;
        public String tip_desc;
        public String con_desc;
        public String loc_desc;
        public String pro_desc;
        public String prp_dom;
        public Integer superficie_total;
        public Integer superficie_cubierta;

        @Column(name = "precio_dolares")
        public Integer prp_pre_dol;
        public Integer oportunidad_dolares;

        public String agua;
        public String luz;
        public String gas;
        public String baños;
        public String cochera;
        public String dormitorios;

        public String prp_lat;
        public String prp_lng;
        public String prp_alta;
        public String prp_mod;


        public String usuario_id;
        public String nombre;


        public boolean isEqual(InmoupProperty other){
            return this.id.equals(other.id);
        }
}
