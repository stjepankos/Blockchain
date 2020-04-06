import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class KuncChain {

    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static int difficulty = 3;

    public static void main(String[] args) {

        blockchain.add(new Block("Ovo je prvi block","0"));
        System.out.println("Majn block 1... ");
        blockchain.get(0).mineBlock(difficulty);

        blockchain.add(new Block("Ovo je drugi block",blockchain.get(blockchain.size()-1).hash));
        System.out.println("Majn block 2... ");
        blockchain.get(1).mineBlock(difficulty);

        blockchain.add(new Block("Ovo je treci block", blockchain.get(blockchain.size()-1).hash));
        System.out.println("Majn block 3... ");
        blockchain.get(2).mineBlock(difficulty);


        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println("The blockchain: ");
        System.out.println(blockchainJson);

    }

    public static boolean isChainValid(){
        Block currentBlock;
        Block previousBlock;

        for (int i = 1;i<blockchain.size();i++){
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);
            String hashTarget = new String(new char[difficulty]).replace('\0', '0');

            //compare registered hash and calculated hash:
            if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
                System.out.println("Current Hashes not equal");
                return false;
            }
            //compare previous hash and registered previous hash
            if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
                System.out.println("Previous Hashes not equal");
                return false;
            }
            //check if hash is solved
            if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                return false;
            }

        }
        return true;
    }
}
