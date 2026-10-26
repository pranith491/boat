// For local development, Spring Boot runs on port 8080. Update API_BASE after deployment.
const API_BASE = "http://localhost:8080/api";
const authNav = document.getElementById("authNav");
const authForm = document.getElementById("authForm");
const authTitle = document.getElementById("authTitle");
const nameField = document.getElementById("nameField");
const authName = document.getElementById("authName");
const authEmail = document.getElementById("authEmail");
const authPassword = document.getElementById("authPassword");
const authSubmit = document.getElementById("authSubmit");
const authMessage = document.getElementById("authMessage");
const bookingForm = document.getElementById("bookingForm");
const bookingMessage = document.getElementById("bookingMessage");
let registerMode = false;
let token = localStorage.getItem("boatToken");
let currentUser = JSON.parse(localStorage.getItem("boatUser") || "null");
const price = 1499;
const today = new Date();
document.getElementById("bookingDate").min = `${today.getFullYear()}-${String(today.getMonth()+1).padStart(2,"0")}-${String(today.getDate()).padStart(2,"0")}`;
function updateAuthUI(){authNav.textContent=token?"Logout":"Login";authNav.href=token?"#":"#login";if(token){authNav.onclick=(e)=>{e.preventDefault();logout();};}else{authNav.onclick=null;}if(currentUser){document.getElementById("bookingName").value=currentUser.name||"";}}
function showToast(msg){const t=document.getElementById("toast");t.textContent=msg;t.classList.add("show");setTimeout(()=>t.classList.remove("show"),3200);}
function logout(){token=null;currentUser=null;localStorage.removeItem("boatToken");localStorage.removeItem("boatUser");updateAuthUI();showToast("You have been logged out.");}
document.getElementById("menuToggle").addEventListener("click",()=>document.getElementById("navLinks").classList.toggle("open"));
document.querySelectorAll("#navLinks a").forEach(a=>a.addEventListener("click",()=>document.getElementById("navLinks").classList.remove("open")));
document.getElementById("quantity").addEventListener("input",e=>{let q=Math.max(1,Math.min(10,Number(e.target.value)||1));document.getElementById("totalPrice").textContent="₹"+(q*price).toLocaleString("en-IN");});
document.getElementById("switchAuth").addEventListener("click",()=>{registerMode=!registerMode;authTitle.textContent=registerMode?"Create account":"Login";authSubmit.textContent=registerMode?"Create account →":"Login →";nameField.classList.toggle("hidden",!registerMode);authName.required=registerMode;document.getElementById("switchAuth").textContent=registerMode?"Already registered? Login":"New here? Create an account";authMessage.textContent="";});
authForm.addEventListener("submit",async e=>{e.preventDefault();authMessage.textContent="Connecting…";const payload={email:authEmail.value.trim(),password:authPassword.value};if(registerMode)payload.name=authName.value.trim();try{const r=await fetch(`${API_BASE}/auth/${registerMode?"register":"login"}`,{method:"POST",headers:{"Content-Type":"application/json"},body:JSON.stringify(payload)});const data=await r.json();if(!r.ok)throw new Error(data.message||"Authentication failed");token=data.token;currentUser={id:data.userId,name:data.name,email:data.email};localStorage.setItem("boatToken",token);localStorage.setItem("boatUser",JSON.stringify(currentUser));updateAuthUI();authMessage.textContent="Success! You can book your product now.";showToast(registerMode?"Account created successfully":"Logged in successfully");authForm.reset();registerMode=false;nameField.classList.add("hidden");authName.required=false;authTitle.textContent="Login";authSubmit.textContent="Login →";document.getElementById("switchAuth").textContent="New here? Create an account";}catch(err){authMessage.textContent=err.message==="Failed to fetch"?"Cannot connect to backend. Start Spring Boot on port 8080.":err.message;}});
bookingForm.addEventListener("submit",async e=>{e.preventDefault();if(!token){bookingMessage.textContent="Please log in or create an account first.";document.getElementById("login").scrollIntoView({behavior:"smooth"});return;}const payload={productName:"boAt Wireless Bluetooth",quantity:Number(document.getElementById("quantity").value),bookingDate:document.getElementById("bookingDate").value,phone:document.getElementById("phone").value.trim()};bookingMessage.textContent="Submitting booking…";try{const r=await fetch(`${API_BASE}/bookings`,{method:"POST",headers:{"Content-Type":"application/json","Authorization":`Bearer ${token}`},body:JSON.stringify(payload)});const data=await r.json();if(r.status===401){logout();throw new Error("Session expired. Please log in again.");}if(!r.ok)throw new Error(data.message||"Booking failed");bookingMessage.textContent=`Booking confirmed! Booking ID: ${data.id}. Total: ₹${Number(data.totalPrice).toLocaleString("en-IN")}`;showToast("Your booking is confirmed!");}catch(err){bookingMessage.textContent=err.message==="Failed to fetch"?"Cannot connect to backend. Check that Spring Boot is running.":err.message;}});
updateAuthUI();
