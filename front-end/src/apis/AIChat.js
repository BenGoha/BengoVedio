import axios from 'axios'
import instance from "./request.js";

export const chatWithAI = (message) => {
    return instance.post('/ai/chat',  {
        message: message,
        sessionId: localStorage.getItem('aiSessionId')
    }, {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
    })
}