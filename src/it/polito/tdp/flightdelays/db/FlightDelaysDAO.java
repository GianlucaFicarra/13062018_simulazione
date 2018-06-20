package it.polito.tdp.flightdelays.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.polito.tdp.flightdelays.model.Airline;
import it.polito.tdp.flightdelays.model.Airport;
import it.polito.tdp.flightdelays.model.AirportIdMap;
import it.polito.tdp.flightdelays.model.Flight;
import it.polito.tdp.flightdelays.model.Tratta;

public class FlightDelaysDAO {

	public List<Airline> loadAllAirlines() {
		String sql = "SELECT id, airline from airlines";
		List<Airline> result = new ArrayList<Airline>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Airline(rs.getString("ID"), rs.getString("airline")));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Airport> loadAllAirports() {
		String sql = "SELECT id, airport, city, state, country, latitude, longitude FROM airports";
		List<Airport> result = new ArrayList<Airport>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Airport airport = new Airport(rs.getString("id"), rs.getString("airport"), rs.getString("city"),
						rs.getString("state"), rs.getString("country"), rs.getDouble("latitude"), rs.getDouble("longitude"));
				result.add(airport);
			}
			
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Flight> loadAllFlights() {
		String sql = "SELECT id, airline, flight_number, origin_airport_id, destination_airport_id, scheduled_dep_date, "
				+ "arrival_date, departure_delay, arrival_delay, air_time, distance FROM flights";
		List<Flight> result = new LinkedList<Flight>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Flight flight = new Flight(rs.getInt("id"), rs.getString("airline"), rs.getInt("flight_number"),
						rs.getString("origin_airport_id"), rs.getString("destination_airport_id"),
						rs.getTimestamp("scheduled_dep_date").toLocalDateTime(),
						rs.getTimestamp("arrival_date").toLocalDateTime(), rs.getInt("departure_delay"),
						rs.getInt("arrival_delay"), rs.getInt("air_time"), rs.getInt("distance"));
				result.add(flight);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	
	//create: seleziono i vertici (gli aereoporti della mia linea)
	public List<Airport> getAllAirportsFromAirline(AirportIdMap airportIdMap, Airline linea) {
		String sql = "select distinct a.id as id, a.airport as airport, a.city as city, a.state as state, a.country as country, a.latitude as lat, a.longitude as lon "+
			     "from airports as a, flights as f " + 
				"where f.AIRLINE= ? " +   //la linea è quella passata
				"and (f.ORIGIN_AIRPORT_ID = a.ID or f.DESTINATION_AIRPORT_ID = a.ID)";
		      //aereoporto per essere nodo deve essere o origine o destinazione della linea
		
		List<Airport> result = new ArrayList<Airport>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, linea.getId());
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Airport airport = new Airport(rs.getString("id"), rs.getString("airport"), rs.getString("city"),
						rs.getString("state"), rs.getString("country"), rs.getDouble("lat"), rs.getDouble("lon"));
				result.add(airportIdMap.get(airport));
			}
				conn.close();
				return result;
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
					
	}
	
	/*
	public List<Flight> getFlightByAirline(Airline linea, AirportIdMap map) {
		String sql = "	SELECT * " + 
				"	FROM flights as f " + 
				"	WHERE f.airline=? ";
		
		List<Flight> list = new ArrayList<>();
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, linea.getId());
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				
				Airport origine=  map.get(rs.getString("origin_airport_id"));
				Airport destinazione= map.get(rs.getString("destination_airport_id"));
				
				if(origine!= null || destinazione!= null) {
					
					
				
				Flight flight = new Flight(rs.getInt("id"), rs.getString("airline"), rs.getInt("flight_number"),
						map.get(rs.getString("origin_airport_id")), map.get(rs.getString("destination_airport_id")),
						rs.getTimestamp("scheduled_dep_date").toLocalDateTime(),
						rs.getTimestamp("arrival_date").toLocalDateTime(), rs.getInt("departure_delay"),
						rs.getInt("arrival_delay"), rs.getInt("air_time"), rs.getInt("distance"));
				list.add(flight); //alla lista inserisco oggetto già presente o appena aggiunta nell'idmap
					
					
					
				}
				
				
			}
			conn.close();
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	*/

	//carico archi
	public List<Tratta> getTratte(AirportIdMap map, Airline linea) {
	
		String sql = "SELECT DISTINCT avg(ARRIVAL_DELAY) AS media, origin_airport_id, destination_airport_id " + 
				"FROM flights AS f " + 
				"WHERE f.AIRLINE=? " + 
				"GROUP BY origin_airport_id, destination_airport_id ";
		
		List<Tratta> list = new ArrayList<>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, linea.getId());
			ResultSet rs = st.executeQuery();

			
			while (rs.next()) {
				
				Airport origine=  map.get(rs.getString("origin_airport_id"));
				Airport destinazione= map.get(rs.getString("destination_airport_id"));
				
				if(origine!= null || destinazione!= null) {
					
				Tratta tratta= new Tratta(rs.getDouble("media"), origine, destinazione);
				list.add(tratta); //alla lista inserisco oggetto già presente o appena aggiunta nell'idmap
				}
				
				
			}
			conn.close();
			return list;
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	 //trova primo volo dopo data passata, da questo aereoporto, verso aereoporto della linea
	public Flight findFirstFlight(Airline linea, String idPartenza, LocalDateTime dataPartenza) {
		String sql = "SELECT * " + 
			 	 "FROM flights " + 
			 	 "WHERE AIRLINE = ? " +
			 	 "AND ORIGIN_AIRPORT_ID = ? " + 
			 	 "AND SCHEDULED_DEP_DATE > ? " + 
			 	 "ORDER BY SCHEDULED_DEP_DATE " +
			 	 "LIMIT 1 "; 
			
	try {
		Connection conn = ConnectDB.getConnection();
		PreparedStatement st = conn.prepareStatement(sql);
		st.setString(1, linea.getId());
		st.setString(2, idPartenza);
		st.setString(3, dataPartenza.toString());
		ResultSet rs = st.executeQuery();
		
		Flight flight = null;
		if (rs.next()) {
		
			flight = new Flight(rs.getInt("id"), rs.getString("airline"), rs.getInt("flight_number"),
				rs.getString("origin_airport_id"), rs.getString("destination_airport_id"),
				rs.getTimestamp("scheduled_dep_date").toLocalDateTime(),
				rs.getTimestamp("arrival_date").toLocalDateTime(), rs.getInt("departure_delay"),
				rs.getInt("arrival_delay"), rs.getInt("air_time"), rs.getInt("distance"));
		}
		
		conn.close();
		return flight;
		
	} catch (SQLException e) {
	e.printStackTrace();
	System.out.println("Errore connessione al database");
	throw new RuntimeException("Error Connection Database");
	}
	}

	
	
}
