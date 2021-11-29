# ethereum-hsm-signer

This library provides an abstraction in the

## Install
In order to use the library you need to include it as a dependency in your project:  
**Maven**
```xml
<dependency>
	<groupId>io.github.alberto-hernandez</groupId>  
	<artifactId>ethereum-hsm-signer</artifactId>  
	<version>0.6.0-SNAPSHOT</version>
</dependency>
```  
**Gradle:**
```gradle
io.github.alberto-hernandez:ethereum-hsm-signer:0.6.0-SNAPSHOT
```  
After that, build your project.

## Configuration
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
    
HashicorpMemorySignerProvider hashicorpMemorySignerBuilder =  
    new HashicorpMemorySignerProvider(kvResolver, secretId);  
  
Signer signer =  
    hashicorpMemorySignerBuilder.get(  
        Address.fromHexString("0xef678007d18427e6022059dbc264f27507cd1ffc"));
// Returns the private key allocated at the secretId Path defines which derivated address should match with the address parametrized
        
```  


