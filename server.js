const express = require("express");
const http = require("http");
const { Server } = require("socket.io");
const path = require("path");

const app = express();
const server = http.createServer(app);

const io = new Server(server, {
  cors: {
    origin: "*"
  }
});

const rooms = new Map();

app.use(express.static(__dirname));

app.get("/", (req, res) => {
  res.sendFile(path.join(__dirname, "index.html"));
});

io.on("connection", (socket) => {

  console.log("User connected:", socket.id);

  // Phone A room बनाएगा
  socket.on("create-room", () => {

    let code;

    do {
      code = Math.floor(
        100000 + Math.random() * 900000
      ).toString();
    } while (rooms.has(code));

    rooms.set(code, {
      host: socket.id,
      guest: null
    });

    socket.join(code);
    socket.roomCode = code;
    socket.role = "A";

    socket.emit("room-created", code);

    console.log("Room created:", code);
  });


  // Phone B room join करेगा
  socket.on("join-room", (code) => {

    code = String(code).trim();

    if (!rooms.has(code)) {
      socket.emit(
        "room-error",
        "यह Pair Code मौजूद नहीं है।"
      );
      return;
    }

    const room = rooms.get(code);

    if (room.guest) {
      socket.emit(
        "room-error",
        "इस Room में Phone B पहले से जुड़ा है।"
      );
      return;
    }

    room.guest = socket.id;

    rooms.set(code, room);

    socket.join(code);
    socket.roomCode = code;
    socket.role = "B";

    socket.emit("joined-room", code);

    io.to(room.host).emit("peer-connected");

    console.log("Phone B joined:", code);
  });


  // Disconnect
  socket.on("disconnect", () => {

    console.log(
      "User disconnected:",
      socket.id
    );

    const code = socket.roomCode;

    if (!code) return;

    const room = rooms.get(code);

    if (!room) return;

    if (room.host === socket.id) {

      rooms.delete(code);

    } else if (room.guest === socket.id) {

      room.guest = null;
      rooms.set(code, room);
    }
  });

});

const PORT = process.env.PORT || 3000;

server.listen(PORT, () => {
  console.log(
    `Server running on port ${PORT}`
  );
});
