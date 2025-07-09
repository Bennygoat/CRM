// src/firebase.js
import { initializeApp } from "firebase/app";
import { getAuth, GoogleAuthProvider, FacebookAuthProvider } from "firebase/auth";
import { getAnalytics } from "firebase/analytics"; // 可選，加 GA 就留著

const firebaseConfig = {
  apiKey: "AIzaSyCLHNMty1fKH4VAkuHHXysCFzjqKcG8Auo",
  authDomain: "crm-project-id.firebaseapp.com",
  projectId: "crm-project-id",
  storageBucket: "crm-project-id.appspot.com",
  messagingSenderId: "759063515521",
  appId: "1:759063515521:web:f02cd24d79a11f691e5880",
  measurementId: "G-M5SFP5GJF6" // 這個只有要用 GA 才會用到
};

// 初始化 Firebase App
const app = initializeApp(firebaseConfig);

// 初始化 Firebase 功能
const auth = getAuth(app); // 必要：登入功能
const googleProvider = new GoogleAuthProvider();
const facebookProvider = new FacebookAuthProvider();

const analytics = getAnalytics(app); // 可選：需要就用，不用就註解

export { auth, googleProvider, facebookProvider, analytics };