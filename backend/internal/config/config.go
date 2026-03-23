package config

import "os"

const (
	defaultPort     = "8080"
	defaultMongoURI = "mongodb://root:password@localhost:27017/finance_tracker?authSource=admin"
)

type Config struct {
	Port     string
	MongoURI string
}

func Load() Config {
	port := os.Getenv("PORT")
	if port == "" {
		port = defaultPort
	}

	mongoURI := os.Getenv("MONGO_CONNECTION_STRING")
	if mongoURI == "" {
		mongoURI = defaultMongoURI
	}

	return Config{
		Port:     port,
		MongoURI: mongoURI,
	}
}
