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
    const {rf} = req.body;

    if (!rf) {
      return res.status(400).send({ erro: "RF não encontrado" });
    }

    const ref = db.ref('usuarios');
    const snapshot = await ref.orderByChild('rf').equalTo(rf).once('value');

    if (!snapshot.exists()) {
      return res.status(401).send('RF não encontrado');
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

app.get('/tela_registros', async (req, res) => {
  const rf = req.query.rf;

  if (!rf) {
    return res.status(400).send('RF é obrigatório para buscar os registros.');
  }

  try {
    const ref = db.ref('registrosPerigo');
    const snapshot = await ref.orderByChild('rf').equalTo(rf).once('value');

    const registros = [];
    snapshot.forEach(child => {
      registros.push({
        id: child.key,
        ...child.val()
      });
    });

    res.status(200).send(registros);
  } catch (err) {
    console.error('Erro ao buscar registros:', err);
    res.status(500).send('Erro ao buscar registros');
  }
});


app.post('/registrar', async (req, res) => {
  try {
    const { rf, descricao, status, gravidade, local, geo, fotoUrl } = req.body;

    if (!rf || !descricao || !status || !gravidade || !local || !geo) {
      return res.status(400).send('Campos obrigatórios estão faltando.');
    }

    const novoRegistro = {
      rf,
      descricao,
      status,
      gravidade,
      local,
      geo, //fazer por mapa
      fotoUrl: fotoUrl || '', //foto do lugar
      data: new Date().toISOString()
    };

    const ref = db.ref('registrosPerigo');
    const newRef = ref.push();
    await newRef.set(novoRegistro);

    res.status(200).send({ id: newRef.key, ...novoRegistro });
  } catch (err) {
    console.error('Erro ao salvar registro:', err);
    res.status(500).send('Erro ao salvar registro');
  }
});


app.listen(3000, '0.0.0.0', () => {
  console.log('API rodando em http://0.0.0.0:3000');
})

