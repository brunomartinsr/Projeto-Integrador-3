const express = require('express');
const cors = require('cors');
const admin = require('firebase-admin')
const serviceAccount = require('./firebase/serviceAccountKey.json')

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: 'https://worksafe-cd7fb-default-rtdb.firebaseio.com/'
})

const db = admin.database()
const app = express()
app.use(cors())
app.use(express.json())

app.get('/', (req, res) => {
  res.send('API está online');
});

app.post('/cadastro', async (req, res) => {
  try {
    const { nome, setor, cargo, rf } = req.body

    if (!rf) {
      return res.status(400).send('RF é obrigatório')
    }

    const ref = db.ref('usuarios');
    const snapshot = await ref.orderByChild('rf').equalTo(rf).once('value')

    if (snapshot.exists()) {
      return res.status(409).send('RF já cadastrado');
    }

    const newUserRef = ref.push();
    await newUserRef.set({ nome, setor, cargo, rf })

    res.status(200).send({ id: newUserRef.key })
  } catch (err) {
    console.error('Erro ao salvar dados:', err)
    res.status(500).send('Erro ao salvar dados')
  }
})


app.post('/login', async (req, res) =>{
  try{
    const {usuario} = req.body;

    if (!usuario) {
      return res.status(400).send({ erro: "Usuário não encontrado" });
    }

    const ref = db.ref('usuarios');
    const snapshot = await ref.orderByChild('usuario').equalTo(usuario).once('value');

    if (!snapshot.exists()) {
      return res.status(401).send('Usuário não encontrado');
    }

    let idUsuario = null;
    let dadosUsuario = null;

    snapshot.forEach((childSnapshot) => {
      idUsuario = childSnapshot.key;
      dadosUsuario = childSnapshot.val();
    });

    res.status(200).send({
      id: idUsuario,
      usuario: dadosUsuario
    });
  } catch (err) {
    console.error('Erro ao fazer login:', err);
    res.status(500).send('Erro ao fazer login');
  }
})

app.listen(3000, '0.0.0.0', () => {
  console.log('API rodando em http://0.0.0.0:3000');
})

