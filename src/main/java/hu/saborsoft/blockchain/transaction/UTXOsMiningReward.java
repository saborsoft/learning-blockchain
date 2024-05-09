package hu.saborsoft.blockchain.transaction;

import java.security.PublicKey;

public class UTXOsMiningReward extends UTXO {

    private static final long serialVersionUID = -5799453224153994156L;

    public UTXOsMiningReward(String parentTransactionID, PublicKey sender,
                             PublicKey receiver, double fundToTransfer) {
        super(parentTransactionID, sender, receiver, fundToTransfer);
    }

    @Override
    public boolean isMiningReward() {
        return true;
    }
}
