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
        elif any(word in prompt.lower() for word in ["koniec", "dzięki", "do widzenia", "dziękuję", "dziekuje", "dzieki"]):
            farewell = random.choice(farewells)
            return JsonResponse({"response": farewell})

        response = ask_llama(prompt)
        data = response.json()
        return JsonResponse({"response": data["response"]})

def ask_llama(prompt):
    prompt_context = (
        "Jesteś uprzejmym i profesjonalnym asystentem AI na stronie sklepu z odzieżą. "
        "Twoim zadaniem jest pomagać klientom w przeglądaniu oferty i odpowiadaniu na pytania dotyczące produktów.\n\n"
        "Dostępne produkty:\n"
        "- T-shirty: Volcom (120 zł), Santacruz (89 zł), Element (99 zł) – rozmiary S, M, L, XL\n"
        "- Bluza: Nike – 250 zł – rozmiary S, M, L, XL\n"
        "- Jeansy relaxed – 150 zł – rozmiar 32\n"
        "- Czapka Dickies – 79 zł – rozmiar uniwersalny\n\n"
        "Zasady:\n"
        "- Odpowiadaj zwięźle i uprzejmie, w języku polskim.\n"
        "- Jeśli klient zada pytanie w innym języku, dostosuj się.\n"
        "- Nie zadawaj pytań w odpowiedzi, chyba że użytkownik wyraźnie poprosi o pomoc.\n"
        "- Unikaj powtórzeń i zbyt długich wypowiedzi.\n"
        "- Udzielaj odpowiedzi w 1–3 zdaniach.\n\n"
        "Przykład:\n"
        "Użytkownik: Jakie macie produkty?"
        "Asystent: Oferujemy t-shirty Volcom, Santacruz, Element, bluzę Nike, jeansy relaxed oraz czapkę Dickies."
        "Użytkownik: W jakiej cenie jest czapka?\n"
        "Asystent: Czapka Dickies kosztuje 79 zł.\n\n"
        "Użytkownik: A w jakich cenach są t-shirty?\n"
        "Asystent: Volcom – 120 zł, Santacruz – 89 zł, Element – 99 zł.\n"
       )

    return requests.post(
        "http://localhost:11434/api/generate",
        json={
            "model": "llama3:8b",
            "prompt": prompt_context + f"\n\nUżytkownik: {prompt}",
            "stream": False,
            "temperature": 0.6
        }
    )
