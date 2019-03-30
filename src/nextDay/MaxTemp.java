package nextDay;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*
 * @Author: Sam's Devil(ANUPAM MISHRA)
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray; 
import org.json.simple.JSONObject;

public class MaxTemp {
	
	// function to get city ID from file citylist.json
	public static String getCityCode(String city) {
		JSONParser jsonParser = new JSONParser();
		
		// File containing list of all city, country, city code, etc
		// citylist.json is bulk data of openweather.org
		try (FileReader reader = new FileReader("citylist.json")) {
			Object obj1 = jsonParser.parse(reader);
			JSONArray cityList = (JSONArray) obj1;
			for(int i=0; i<cityList.size(); i++) {
				JSONObject e2= (JSONObject)cityList.get(i);
				
				// Compare users input with list of city ignoring extra spaces and case sensitivity
				if(e2.get("name").toString().replaceAll("\\s+","").equalsIgnoreCase(city.replaceAll("\\s+",""))) {
					
					// Return ID of city if searched city is found
					return e2.get("id").toString();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		// Return nothing of city if searched city is not found
		return "";
	}
	
	public static String getNextDayDate() {
		Date dt = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(dt);
		c.add(Calendar.DATE, 1);
		dt = c.getTime();
		String pattern = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		
		String date = simpleDateFormat.format(dt);
		return date;
	}

	public static double getMaxTemp(String id) {
		return getMaxTemp(id, 0, 24);
	}
	public static double getMaxTemp(String id, int from, int to) {
		
		// get City id if argument is city name
		if(getCityCode(id) != "") {
			id=getCityCode(id);
		}
				
		String URL_API = "https://api.openweathermap.org/data/2.5/forecast?id=" + id;
		String API_KEY = "eb8b1a9405e659b2ffc78f0a520b1a46";

		// get JSON from https://openweathermap.org/ using city code
		String url = URL_API + "&appid=" + API_KEY;
		URL obj;
		try {
			obj = new URL(url);
			HttpURLConnection con;
			con = (HttpURLConnection) obj.openConnection();
			int responseCode = 0;
			try {
				responseCode = con.getResponseCode();
			} catch(UnknownHostException he) {
				JOptionPane.showMessageDialog(null, "Check your Internet Connection");
				return 0;
			}
			catch (Exception e) {
				// TODO: handle exception
			}
			if(responseCode==200) {
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}				
				in.close();				
				
				org.json.JSONObject myresponse = new org.json.JSONObject(response.toString());
				if(myresponse.get("cod").equals("200")) {
					String date = getNextDayDate();
					
					// variable that stores maximum temperature from list
					double max=0.0;
					org.json.JSONArray jsarr=(org.json.JSONArray)myresponse.get("list");
					for(int i=0; i<jsarr.length();i++) {
						org.json.JSONObject js=(org.json.JSONObject) jsarr.get(i);
						String[] darr=js.get("dt_txt").toString().split(" ");
						String[] tarr=darr[1].split(":");
						if(darr[0].equals(date) && (Integer.parseInt(tarr[0]) >= from) && (Integer.parseInt(tarr[0]) <= to)) {
							//System.out.println(js.get("dt_txt"));
							double jsar=js.getJSONObject("main").getDouble("temp_max");
							if(jsar>max) {
								max=jsar;
							}
						}
					}
					if(max != 0) {
						// return max temperature in Kelvin
						return max;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
  
		//return 0 if nothing found
		return 0;
	}
	
	
	// Driver function to show demo of API
	public static void main(String[] args) {

		JFrame f=new JFrame("Next Day Maximum Temperature");
		JLabel lblCityName = new JLabel("City Name : ");
		JTextField tfCityName = new JTextField();
		JRadioButton rbWholeDay=new JRadioButton("Whole Day");
		JRadioButton rbTime=new JRadioButton("Time(in Hours)");
		ButtonGroup bg=new ButtonGroup();
		JButton btnGet = new JButton("Get Data"); 
		JLabel lblFrom = new JLabel("From : ");
		JLabel lblTo = new JLabel("To : ");
		SpinnerModel fromHourValue = new SpinnerNumberModel(0, 0, 23, 1);
		SpinnerModel toHourValue = new SpinnerNumberModel(0, 0, 24, 1);
		JSpinner spnFromHour = new JSpinner(fromHourValue);
		JSpinner spnToHour = new JSpinner(toHourValue);
		JTextArea taSummary = new JTextArea();
		
		lblCityName.setBounds(50, 50, 100, 30);
		tfCityName.setBounds(150, 50, 150, 30);
		rbWholeDay.setBounds(15,100,85,30);
		rbTime.setBounds(100,100,120,30);
		lblFrom.setBounds(250, 100, 50, 30);
		spnFromHour.setBounds(350, 100, 50, 30);
		lblTo.setBounds(420, 100, 50, 30);
		spnToHour.setBounds(470, 100, 50, 30);
		btnGet.setBounds(50, 200, 100, 30);
		taSummary.setBounds(200, 200, 400, 80);
		
		bg.add(rbWholeDay);bg.add(rbTime);
		rbWholeDay.setSelected(true);
		spnFromHour.setEnabled(false);
		spnToHour.setEnabled(false);
		taSummary.setEditable(false);
		
		f.add(lblCityName); f.add(tfCityName);
		f.add(rbWholeDay); f.add(rbTime);
		f.add(lblFrom); f.add(lblTo);
		f.add(spnFromHour); f.add(spnToHour);
		f.add(btnGet); f.add(taSummary);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setBounds(250, 150, 700,400);  
	    f.setLayout(null);  
	    f.setVisible(true); 
	    	    	    
	    rbWholeDay.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				spnFromHour.setEnabled(false);
				spnToHour.setEnabled(false);
			}
		});
	    
		rbTime.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				spnFromHour.setEnabled(true);
				spnToHour.setEnabled(true);
			}
		});
	    
	    btnGet.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String city = tfCityName.getText();
				
				// Valid if city was filled
				if (city.equals(null) || city.isEmpty()) {
					taSummary.setText("City name is required!");
					JOptionPane.showMessageDialog(null, "City name is required!");
					return;
				} else if(rbWholeDay.isSelected()) {
						
					// get Maximum Temperature in Kelvin
					double maxtemp = getMaxTemp(getCityCode(city), 0, 24);
					if(maxtemp != 0) {
						DecimalFormat decform = new DecimalFormat("##.000");
						taSummary.setText("Max Temp on " + getNextDayDate() + " in Kelvin is : " + maxtemp + "\n\nIn Celsius is : " + decform.format(maxtemp-273.15) + "C");
					}
					else {
						taSummary.setText("City not found!");
					}
				}
				else if(rbTime.isSelected()) {
					int from = (Integer) spnFromHour.getValue();
					int to = (Integer) spnToHour.getValue();
					if( from >= to) {
						taSummary.setText("FROM value cannot be greater than or equal to TO value");
						JOptionPane.showMessageDialog(null, "FROM value cannot be greater than or equal to TO value");
					}
					else {
						// get Maximum Temperature in Kelvin
						double maxtemp = getMaxTemp(getCityCode(city), from, to);
						if(maxtemp != 0) {
							DecimalFormat decform = new DecimalFormat("##.000");
							taSummary.setText("Max Temp on " + getNextDayDate() + " from " + fromHourValue.getValue() +
												" hours  to " + toHourValue.getValue() + " hours in Kelvin is : " + maxtemp + 
												"\n\nIn Celsius is : " + decform.format(maxtemp-273.15) + "C");
						}
						else {
							System.out.println("City not found!");
						}
					}
				}
			}
		});
	}
}
