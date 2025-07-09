// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAuth, GoogleAuthProvider, FacebookAuthProvider } from "firebase/auth";

// Your web app's Firebase configuration
const firebaseConfig = {
  apiKey: "AIzaSyC6cflmVissP1-bisg2Qz2RYiUqHF8pEOs",
  authDomain: "loginproject-7273e.firebaseapp.com",
  projectId: "loginproject-7273e",
  storageBucket: "loginproject-7273e.appspot.com",
  messagingSenderId: "606423973024",
  appId: "1:606423973024:web:b3788cd7a040364671ddc5",
  measurementId: "G-TFRS99G153"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const auth = getAuth(app);
const googleProvider = new GoogleAuthProvider();
const facebookProvider = new FacebookAuthProvider();

export { auth, googleProvider, facebookProvider };
