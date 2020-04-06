import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.SQLOutput;
import java.util.ArrayList;

public class Transaction {
    public String transactionID;
    public PublicKey sender;
    public PublicKey reciepient;
    public float value;
    public byte[] signature;

    public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    private static int sequence = 0;

    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.reciepient = to;
        this.value = value;
        this.inputs = inputs;
    }

    private String calculateHash() {
        sequence++; // Increase sequence so 2 identical transactions don't have same hash
        return StringUtil.applySha256(
                StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(reciepient) +
                        Float.toString(value)+
                        sequence
        );
    }
    public void generateSignature(PrivateKey privateKey){
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient);
        signature = StringUtil.applyECDSASig(privateKey,data);
    }

    public boolean verifySignature(){
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient);
        return StringUtil.verifyECDSASig(sender,data,signature);
    }

    public boolean processTransaction(){
        if (verifySignature()==false){
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }
        for (TransactionInput i : inputs){
            i.UTXO = KuncChain.UTXOs.get(i.transactionOutputId);
        }

        if (getInputsValue()< KuncChain.minimumTransaction){
            System.out.println("#Transaction Inputs to small: "+ getInputsValue());
            return false;
        }

        float leftOver = getInputsValue() - value;
        transactionID = calculateHash();
        outputs.add(new TransactionOutput(this.reciepient, value, transactionID));
        outputs.add(new TransactionOutput(this.sender,leftOver,transactionID));

        for(TransactionOutput o: outputs){
            KuncChain.UTXOs.put(o.id, o);
        }

        for (TransactionInput i: inputs){
            if (i.UTXO==null){
                continue;
            }
            KuncChain.UTXOs.remove(i.UTXO.id);
        }
        return true;
    }

    public float getInputsValue(){
        float total =0;
        for (TransactionInput i:inputs){
            if (i.UTXO==null){
                continue;
            }
            total +=i.UTXO.value;
        }
        return total;
    }

    public float getOutputsValue(){
        float total = 0;
        for (TransactionOutput o: outputs){
            total+=o.value;
        }
        return total;
    }
}





















