
# ethereum-hsm-signer

This library facilitates to retrieve a private key from a SSM (Hashicorp Vault) and a Signer abstraction to create an ECDSA signature and not expose the private key.

## SSM (Hashicorp Vault)
This library supports in the initial version the Hashicorp Vault KV engine with AppRole Authentication method. Please, take a look to the [HashicorpVaultKV.md](./HashicorpVaultKV.md) in order to see a sample of instructions to instantiate a KV engine vault with a private key initialized.


## Configuration
In order to use the library you need to include it as a dependency in your project:

**Maven**
```xml
<dependency>
	<groupId>io.github.alberto-hernandez</groupId>  
	<artifactId>ethereum-hsm-signer</artifactId>  
	<version>0.8.0</version>
</dependency>
```  
**Gradle:**
```gradle
io.github.alberto-hernandez:ethereum-hsm-signer:0.8.0
```  
After that, build your project.

## Initialization
In order to use the library you need to include it as a dependency in your project:

```java

// Create the WebClient Connection against Hashicorp Vault, example: http://localhost:8200
WebClient webclient =  
    HashicorpConnectionBuilder.builder()  
        .basicParameters(new BasicParameters("localhost", 8200))  
        .tlsOptions(Optional.empty())  
        .vertx(Vertx.vertx())  
        .build()  
        .buildConnection();

// Create a KV Resolver with AppRoleAuthentication        
HashicorpKvResolver kvResolver =  
    new HashicorpKvResolver(  
        webclient,  
        new AppRoleAuthentication(Optional.of(token), webclient, new AppRole("", "")));

// Retrieves the private key Id allocated at: /my-kv-engine-name/my-private-key
SecretId secretId =  
    new SecretId("/v1/my-kv-engine-name/data/my-private-key", Optional.of("privatekey"));

SecuredSignerProvider securedSignerProvider =  
    new HashicorpMemorySignerProvider(kvResolver, secretId);  
  
Signer signer =
    securedSignerProvider.get(  
        Address.fromHexString("0xef678007d18427e6022059dbc264f27507cd1ffc"));
// Returns the private key allocated at the secretId Path defines which derivated address should match with the address parametrized
        
```  

## Use
Once that the SignerProvider is created, it can be used using the next snipped:

```java

Bytes transactionSerialized =  // Serialize the transaction/message to a org.apache.tuweni.bytes.Bytes;  
Bytes signature = signer.sign(transactionSerialized);

Bytes v_component = SignatureUtils.getV(signature);  
Bytes r_component = SignatureUtils.getR(signature);  
Bytes s_component = SignatureUtils.getS(signature);

```
