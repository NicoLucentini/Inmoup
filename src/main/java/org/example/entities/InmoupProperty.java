package org.example.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Table(name = "Properties")
@Entity
public class InmoupProperty implements Serializable{

        public InmoupProperty(){}
        public String latLong;
        public void doUrl(){
            String inmobiliaria = (usuario_id + "-"+nombre).replaceAll(" ", "-");
            String tipoCasa = (tip_desc + "-en-" + con_desc + "-en-" +prp_dom).replaceAll(" ", "-");
            url = "inmoup.com.ar/" + inmobiliaria + "/inmuebles/" + propiedad_id + "/ficha/" + tipoCasa + "?btid=" + id ;
        }
        public void doMap(){
                mapsLink = "https://www.google.com/maps?q=" + prp_lat + "," + prp_lng;
        }
        public void doLatLong(){
                latLong = prp_lat != null ? prp_lat : "" + "," + prp_lng != null ? prp_lng : "";
        }
        public void doDiasPublicada(){
                LocalDate localDate = LocalDate.now();
                String date = prp_alta != null ? prp_alta.split(" ")[0] : localDate.toString();
                LocalDate propDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                diasPublicada = Math.abs((int)ChronoUnit.DAYS.between(localDate, propDate));
        }
        public Integer diasPublicada;

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
        public String mapsLink;
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

        public static String convertToDMS(double coordinate, boolean isLatitude) {
                String direction;
                if (isLatitude) {
                        direction = (coordinate < 0) ? "S" : "N";
                } else {
                        direction = (coordinate < 0) ? "W" : "E";
                }

                double absCoord = Math.abs(coordinate);
                int degrees = (int) absCoord; // Get whole degrees
                double minutesDecimal = (absCoord - degrees) * 60; // Get decimal minutes
                int minutes = (int) minutesDecimal; // Get whole minutes
                double seconds = (minutesDecimal - minutes) * 60; // Get seconds

                return String.format("%d°%d'%.1f\"%s", degrees, minutes, seconds, direction);
        }
}
