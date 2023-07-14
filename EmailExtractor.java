import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class EmailExtractor {

    public EmailExtractor() {
        super();
    }

    public boolean processFile(String file, String delimiter) 
    {
        String outputFile = "result.txt"; // Replace with the desired output file path

        // Extract email addresses from the CSV file and write them to the output file
        try (BufferedReader br = new BufferedReader(new FileReader(file));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
            String line;
            Set<String> uniqueEmails = new HashSet<>();

            // Read the CSV file line by line
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(delimiter);

                // Extract email addresses from each column
                for (String column : columns) 
                {
                    // Split by semicolon to extract multiple email addresses from the same column
                    String[] addresses = column.split(";");

                    // Add the extracted email addresses to the set of unique email addresses
                    for (String address : addresses) {
                        String trimmedAddress = address.trim().replace("\"","").toLowerCase();
                        if (isValidEmail(trimmedAddress)) 
                        {
                            uniqueEmails.add(trimmedAddress);
                        }
                    }
                }
            }

            // Write the unique email addresses to the output file
            for (String email : uniqueEmails) {
                bw.write(email);
                bw.newLine();
            }

        } catch (IOException e) {
            return false;
        }
        return true;
    }


    private boolean isValidEmail(String email) 
    {
        var regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" 
        + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

        // Check if the email is valid
        if(!email.matches(regexPattern))
            return false;

        // Check if the email contains a blacklisted word from the file "blacklist.txt"
        File blacklistFile = new File(System.getProperty("user.dir") + "/blacklist.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(blacklistFile))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (email.contains(line)) {
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }
}
