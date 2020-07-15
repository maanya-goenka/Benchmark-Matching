import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class benchmarkMatcher {

   /**
   * Iterate through each line of input.
   */
   public static void main(String[] args) throws IOException {
       InputStreamReader reader = new InputStreamReader(System.in, StandardCharsets.UTF_8);
       BufferedReader in = new BufferedReader(reader);
       String line;
       while ((line = in.readLine()) != null) {
           benchmarkMatcher.matchBenchmark(line);
       }
   }
  
   public static void matchBenchmark(String input) {
      
        String[] data = input.split(":");
       // get the portfolio assets from input
       //String portfolio = input.substring(0,input.indexOf(":"));
       // get the benchmark assets from input
       //String benchmark = input.substring(input.indexOf(":")+1);
      
       // split the portfolio assets using "|" as the delimiter , use \\ as its a meta character to access each asset
       String portfolioAssetsStr[] = data[0].split("\\|");
       String benchmarkAssetsStr[] = data[1].split("\\|");
      
       // create an array of assets
       Asset portfolioAsset[] = new Asset[portfolioAssetsStr.length];
       Asset benchmarkAsset[] = new Asset[benchmarkAssetsStr.length];
      
       // loop to get the portfolio assets
       for(int i=0;i<portfolioAssetsStr.length;i++)
       {
           String fields[] = portfolioAssetsStr[i].split(",");
           portfolioAsset[i] = new Asset(fields[0],fields[1],Integer.parseInt(fields[2]));
       }
      
       // loop to get the benchmark assets
       for(int i=0;i<benchmarkAssetsStr.length;i++)
       {
           String fields[] = benchmarkAssetsStr[i].split(",");
           benchmarkAsset[i] = new Asset(fields[0],fields[1],Integer.parseInt(fields[2]));
       }
      
      
       boolean found;
       boolean benchmarkFound[] = new boolean[benchmarkAsset.length];
      
       for(int i=0;i<benchmarkAsset.length;i++)
           benchmarkFound[i] = false;
      
       Transaction transactions[] = null; // create an array of output transactions
       // loop to find the transactions needed to make portfolio assets same as benchmark assets
       // consider the assets which are in portfolio assets
       for(int i=0;i<portfolioAsset.length;i++)
       {
           found = false;
           for(int j=0;j<benchmarkAsset.length;j++)
           {
               if(portfolioAsset[i].compareTo(benchmarkAsset[j]) == 0)
               {
                   if(portfolioAsset[i].getShares() > benchmarkAsset[j].getShares())
                   {
                       if(transactions == null)
                           transactions = new Transaction[1];
                       else
                           transactions = Arrays.copyOf(transactions, transactions.length+1);
                       transactions[transactions.length-1] = new Transaction("SELL", new Asset(portfolioAsset[i].getName(),portfolioAsset[i].getType(),portfolioAsset[i].getShares()-benchmarkAsset[j].getShares()));
                   }      
                   else if(portfolioAsset[i].getShares() < benchmarkAsset[j].getShares())
                   {
                       if(transactions == null)
                           transactions = new Transaction[1];
                       else
                           transactions = Arrays.copyOf(transactions, transactions.length+1);
                       transactions[transactions.length-1] = new Transaction("BUY", new Asset(portfolioAsset[i].getName(),portfolioAsset[i].getType(),benchmarkAsset[j].getShares()-portfolioAsset[i].getShares()));
                   }
                   benchmarkFound[j] = true;
                   found = true;
                   break;
               }
           }
          
           if(!found)
           {
               if(transactions == null)
                   transactions = new Transaction[1];
               else
                   transactions = Arrays.copyOf(transactions, transactions.length+1);
               transactions[transactions.length-1] = new Transaction("SELL", portfolioAsset[i]);
           }
       }
          
      
       // loop to include the assets present in benchmark assets but not in portfolio assets in transactions array
       for(int i=0;i<benchmarkFound.length;i++)
       {
           if(!benchmarkFound[i])
           {
               if(transactions == null)
                   transactions = new Transaction[1];
               else
                   transactions = Arrays.copyOf(transactions, transactions.length+1);
               transactions[transactions.length-1] = new Transaction("BUY", benchmarkAsset[i]);
           }
       }
          
       // loop to sort the list of transactions
       int min;
       for(int i=0;i<transactions.length-1;i++)
       {
           min = i;
           for(int j=i+1;j<transactions.length;j++)
           {
               if(transactions[j].getAsset().compareTo(transactions[min].getAsset()) < 0)
                   min = j;
           }
              
           if(min != i)
           {
               Transaction temp = transactions[i];
               transactions[i] = transactions[min];
               transactions[min] = temp;
           }
       }  
          
       // display the output transactions
       for(int i=0;i<transactions.length;i++)
           System.out.println(transactions[i]);
          
   }

} //end Main class

// class representing Asset types
class Asset implements Comparable<Asset>
{
   private String name;
   private String type;
   private int shares;
      
   public Asset(String name, String type, int shares)
   {
       this.name = name;
       this.type = type;
       this.shares = shares;
   }
  
   public String getName()
   {
       return name;
   }
  
   public String getType()
   {
       return type;
   }
  
   public int getShares()
   {
       return shares;
   }

   // used in sorting the assets
   @Override
   public int compareTo(Asset asset) {

       if(name.toLowerCase().compareTo(asset.getName().toLowerCase()) < 0)
           return -1;
       else if(name.toLowerCase().compareTo(asset.getName().toLowerCase()) > 0)
           return 1;
       else
       {
           return(type.toLowerCase().compareTo(asset.getType().toLowerCase()));
       }
   }
  
   public String toString()
   {
       return name+","+type.toUpperCase()+","+shares;
   }
} // end Asset class

// Transaction class to represent the transactions
class Transaction
{
   private String type;
   private Asset asset;
  
   public Transaction(String type, Asset asset)
   {
       this.type = type;
       this.asset = asset;
   }
  
   public String getType()
   {
       return type;
   }
  
   public Asset getAsset()
   {
       return asset;
   }
  
   public String toString()
   {
       return type.toUpperCase()+","+asset.toString();
   }
} // end Transaction class
//end of program