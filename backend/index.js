const express = require('express');
const cors = require('cors');
const admin = require('firebase-admin');
const serviceAccount = require('./firebase/serviceAccountKey.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: 'https://worksafe-cd7fb-default-rtdb.firebaseio.com/'
});

const db = admin.database();
const app = express();
app.use(cors());
app.use(express.json());

app.post('/cadastro', async (req, res) => {
  try {
    const data = req.body;
    const ref = db.ref('usuarios');
    const newUserRef = ref.push();
    await newUserRef.set(data);
    res.status(200).send({ id: newUserRef.key });
  } catch (err) {
    console.error('Erro ao salvar dados:', err);
    res.status(500).send('Erro ao salvar dados');
  }
});

app.listen(3000, () => {
  console.log('API rodando em http://localhost:3000');
});
