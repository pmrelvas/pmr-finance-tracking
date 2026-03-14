print('Initializing database...')

db.auth('root', 'password')
db = db.getSiblingDB('finance_tracker')

db.createUser({
  user: 'root',
  pwd: 'password',
  roles: [{ role: 'readWrite', db: 'finance_tracker' }]
});

print('Database finance_tracker initialized successfully')
