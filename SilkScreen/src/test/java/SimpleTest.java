
import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.PushPayload;
import com.deaking.wallet.core.entity.TxVout;
import com.deaking.wallet.core.entity.UnSpent;
import com.deaking.wallet.core.util.BinaryHexUtil;
import com.deaking.wallet.core.util.JBlowfish;
import com.deaking.wallet.core.util.TimeOutFinal;
import com.deaking.wallet.silkscreen.config.JsonrpcClient;
import com.deaking.wallet.silkscreen.entity.SilkScreenData;
import com.deaking.wallet.silkscreen.util.StringHexUtil;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.codec.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.jhblockchain.crypto.CoinTypes;
import org.jhblockchain.crypto.ECKeyPair;
import org.jhblockchain.crypto.bip32.ExtendedKey;
import org.jhblockchain.crypto.bip39.MnemonicCode;
import org.jhblockchain.crypto.bip44.AddressIndex;
import org.jhblockchain.crypto.bip44.BIP44;
import org.jhblockchain.crypto.bip44.CoinPairDerive;
import org.jhblockchain.crypto.bitcoin.BTCTransaction;
import org.jhblockchain.crypto.exceptions.ValidationException;
import org.jhblockchain.crypto.params.SLUNetworkParameters;
import org.jhblockchain.crypto.utils.HexUtils;
import org.junit.Test;
import org.web3j.crypto.Hash;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import static com.sun.javafx.fxml.expression.Expression.equalTo;
import static org.junit.Assert.assertThat;


public class SimpleTest {

    private final static Gson gson = new GsonBuilder().registerTypeAdapter(Double.class,
            new JsonSerializer<Double>() {
                @Override
                public JsonElement serialize(Double value,
                                             Type theType, JsonSerializationContext context) {

                    // Keep 5 decimal digits only
                    return new JsonPrimitive((new BigDecimal(value)).setScale(5, BigDecimal.ROUND_HALF_UP));
                }
            }).serializeNulls().create();

    public static void main(String[] args) throws Throwable {

        String text = "687474703a2f2f636e79742e62692f232f64657461696c732f323337233437346162313037303038643263363038326638636536386438633865613433";
        if (StringUtils.isNotBlank(text)) {
            System.out.println(1);
            String hex = text.replace("OP_RETURN", "").trim();
            if (hex.length() >= 68) {
                System.out.println(2);
                hex = hex.substring(66, hex.length() - 2);
                String md5 = new String(BinaryHexUtil.HexStringToBinary(hex), Charsets.UTF_8);
                String[] array = md5.split("[;#]");
                if (array.length > 0 && array[array.length - 1].length() == 32) {
                    System.out.println(array[array.length - 1]);
                }
            }
        }


    }

    public static PushPayload buildPushObject_all_all_alert() {
        return PushPayload.alertAll("who am i");
    }

}
