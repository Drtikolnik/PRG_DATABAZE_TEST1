import java.sql.Connection;
import java.sql.*;
import java.sql.DriverManager;
import java.util.Scanner;

void main() {
    Scanner sc = new Scanner(System.in);

String url = "jdbc:mysql://localhost/archeologie";
String user = "root";
String password = "";

System.out.println("--VYHLEDANI POKLADU ZADEJ--");
System.out.println("zadej material");
String materialZadany = sc.nextLine();

System.out.println("zadej hodnotu");
int hodnotaZadana = sc.nextInt();
sc.nextLine();

System.out.println("--VYHLEDANI PODLE STATU--");
System.out.println("zadej stat");
String statZadany = sc.nextLine();

try(Connection conn = DriverManager.getConnection(url, user, password)){
    System.out.println("uspesne pripojeni");


    String nalezyEverythingSELECT = "SELECT * FROM nalezy ORDER BY stari DESC";

    String pokladSELECT = "SELECT * FROM nalezy WHERE material = ? AND hodnota >= ?";

    String statSELECT = "SELECT * FROM lokality WHERE stat = ?";
    String lokalitaNalezSELECT =    "SELECT lokality.nazev, nalezy.nazev, nalezy.stari " +
                                    "FROM lokality " +
                                    "INNER JOIN nalezy ON lokality.id_lokality = nalezy.id_lokality" +
                                    "WHERE id_lokality = ? AND hodnota >= ?" +
                                    "ORDER BY lokality.nazev ASC ";

//vsechno
    try(PreparedStatement stmt = conn.prepareStatement(nalezyEverythingSELECT)){
        //stmt.execute(nalezyEverythingSELECT);
        System.out.println("---VYPIS VSEHO Z NALEZU---");

        try(ResultSet rs = stmt.executeQuery()){
            while(rs.next()){
                String nazev =  rs.getString("nazev");
                String material = rs.getString("material");
                String stari = rs.getString("stari");

                System.out.println(nazev + " | " + material + " | " + stari);
            }
        }


    }catch(SQLException ex){
        System.err.println("problem s sql");
    }


//poklad
    try(PreparedStatement stmt = conn.prepareStatement(pokladSELECT)){
        stmt.setString(1, materialZadany);
        stmt.setInt(2, hodnotaZadana);

        System.out.println("---VYHLEDANI POKLADU---");

        try(ResultSet rs = stmt.executeQuery()){
            ArrayList<String> seznam = new ArrayList<>();
            while(rs.next()){
                String nazev =  rs.getString("nazev");
                String hodnota = rs.getString("hodnota");

                System.out.println(nazev + " | " + hodnota);

                seznam.add(nazev);

            }

            if(seznam.isEmpty()){
                System.err.println("Nenalezen zadny predmet podle zadani");
            }else{
                seznam.clear();
            }

        }

    }catch(SQLException ex){
        System.err.println("problem s sql");
    }


//stat
    try(PreparedStatement stmt = conn.prepareStatement(statSELECT)){
        stmt.setString(1, statZadany);
        System.out.println("---VYHLEDANI PODLE STATU---");

        try(ResultSet rs = stmt.executeQuery()){
            ArrayList<String> seznam = new ArrayList<>();
            while(rs.next()){

                try(PreparedStatement stmt2 = conn.prepareStatement(lokalitaNalezSELECT)){
                    try(ResultSet rs2 = stmt2.executeQuery()){
                        while(rs2.next()){
                            String nazevPredmetu =  rs2.getString("nalezy.nazev");
                            String nazevlokality =  rs2.getString("lokality.nazev");
                            int stariPredmetu =  rs2.getInt("nalezy.stari");
                            System.out.println(nazevPredmetu+ " | " +nazevlokality+ " | " +stariPredmetu);
                        }
                    }

                }catch(SQLException ex){
                    System.err.println("problem s sql");
                }





                String stat = rs.getString("stat");
                seznam.add(stat);

            }

            if(seznam.isEmpty()){
                System.err.println("Nenalezen zadny stat podle zadani");
            }else{
                seznam.clear();
            }

        }

    }catch(SQLException ex){
        System.err.println("problem s sql");
    }







} catch (SQLException e) {
    System.err.println("problem s pripojenim k databazi");
}


}
