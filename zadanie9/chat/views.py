from django.shortcuts import render
import requests
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
import json
import requests
import random

def shop_page(request):
    return render(request, "shop.html")

def chat_page(request):
    return render(request, "index.html")

@csrf_exempt
def chat_view(request):
    greetings = [
        "Cześć! W czym mogę Ci dziś pomóc?",
        "Witaj w naszym sklepie z ubraniami! Jak mogę pomóc?",
        "Hej! Szukasz czegoś konkretnego?",
        "Dzień dobry! Z chęcią odpowiem na pytania o nasze produkty.",
        "Witaj! Jeśli masz jakieś wątpliwości to daj znać!"
    ]

    farewells = [
        "Dzięki za rozmowę! Do zobaczenia!",
        "Miłego dnia i udanych zakupów!",
        "Dziękujemy za odwiedziny – zapraszamy ponownie!",
        "Cieszę się, że mogłem pomóc. Do zobaczenia!",
        "Jeśli będziesz potrzebować pomocy, wróć kiedy chcesz!"
    ]
    
    if request.method == "POST":
        data = json.loads(request.body)
        prompt = data.get("prompt", "")

        if prompt == "start":
            greeting = random.choice(greetings)
            return JsonResponse({"response": greeting})
        elif prompt in ["koniec", "dziekuje"]:
            farewell = random.choice(farewells)
            return JsonResponse({"response": farewell})

        response = ask_llama(prompt)
        data = response.json()
        return JsonResponse({"response": data["response"]})

def ask_llama(prompt):
    promptCoxtext = (
        "Jesteś pomocnym asystentem na stronie sklepu z ubraniami. "
        "Sklep sprzedaje: t-shirty, bluzy, spodnie, czapki z daszkiem. "
        "Dostepne sa 3 t-shirty: Volcom po 120zl, Santacruz po 89zł, Element po 99zł."
        "Wszystkie w rozmiarach S M L XL"
        "Jest tez bluza nikke we wszystkich rozmiarach po 250zl, jeansy z krojem relaxed w rozmiarze 32 po 150zl"
        "Jest równiez dostepna czapka dickies w rozmiarze uniwersalnym po 79zl"
        "Odpowiadaj profesjonalnie, ale przyjaźnie, w języku polskim, chyba ze uzytkownik zada pytanie w innym języku" 
        )

    return requests.post(
        "http://localhost:11434/api/generate",
        json={
            "model": "llama3.2",
            "prompt": promptCoxtext + "\n\n Użytkownik: " + prompt,
            "stream": False
        }
    )