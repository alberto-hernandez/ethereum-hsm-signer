
# Hashicorp Vault Configuration
This is a sample guide to start a configure a vault for **testing** purposes based on the guide of Ruaan Beeker blog (see original [link](https://blog.ruanbekker.com/blog/2019/05/06/setup-hashicorp-vault-server-on-docker-and-cli-guide/))

In this guide we boot up a vault instance and configure it in order to test the ethereum-hsm-signer library:

## Boot-Up Vault

1. Create volume directories
```shell
$ mkdir -p volumes/{config,file,logs}
```
2. Create the Vault Configuration file
```shell
$ cat > volumes/config/vault.json << EOF 
{ 
	"backend": { 
		"file": { 
			"path": "/vault/file" 
		}
	 }, 
	 "listener": { 
		 "tcp": { 
			 "address": "0.0.0.0:8200", 
			 "tls_disable": 1
		} 
	 }, 
	 "ui": true 
} 
EOF
```

3. Create the docker-compose image for launching the image
```shell
$ cat > docker-compose.yml << EOF
version: '2' 
	services: vault: 
	image: vault 
	container_name: vault 
	ports: - "8200:8200" 
	restart: always 
	volumes: 
		- ./volumes/logs:/vault/logs 
		- ./volumes/file:/vault/file 
		- ./volumes/config:/vault/config 
	cap_add: - IPC_LOCK 
	entrypoint: vault server -config=/vault/config/vault.json 
EOF
```
4. Boot-up the Vault
```shell
$ docker-compose up
```

## Initialize Vault

Install the Vault client to interact with the vault

```shell
$  sudo apt install vault
```
Export vault address
```shell
$ export VAULT_ADDR='http://127.0.0.1:8200'
```

Init the VAULT

```shell
$ vault operator init -key-shares=6 -key-threshold=3
```
Take all the unseal keys and the token and saved to an appropiate location. You need to unseal the Vault in order to read/write to the vault. To do this you need to execute this commands with at least 3 keys (key-threshold value) defined previously.

```shell
$ vault operator unseal <key1>
$ vault operator unseal <key2>
$ vault operator unseal <key3>
```
After unseal, you can check the status of the vault with the next command:
```shell
$ vault operator status
```

Login with the ROOT Token
```shell
$ vault login <root_token>
```

## Create Engine (KV)

The next commands initialize the Vault with a KV engine named "blockchain" in which will hold the private keys

```shell
$ vault secrets enable -version=2 -path=blockchain kv
```

Putting the private key (a392604efc2fad9c0b3da43b5f698a2e3f270f170d859912be0d54742275c5f6) into the vault under path my-private-key:

```shell
$ vault kv put blockchain/my-private-key privatekey=a392604efc2fad9c0b3da43b5f698a2e3f270f170d859912be0d54742275c5f6
```
In order to check the that you have store properly the key you can execute the next command:
```shell
$ vault kv get --format=json blockchain/my-private-key 
```

