import json
import boto3


def lambda_handler(event, context):
    # Crea una instancia del cliente de DynamoDB
    dynamodb = boto3.client('dynamodb')

    response = dynamodb.scan(TableName='languages')
    items = response['Items']

    # Ordena los elementos por language_id
    items_sorted = sorted(items, key=lambda x: x['language_id']['S'])

    # Convierte los items a un formato legible
    idiomas = []
    for item in items_sorted:
        idioma = {
            'codIdioma': item['language_id']['S'],
            'labelIdioma': item['language_label']['S']
        }
        idiomas.append(idioma)

    # Retorna los idiomas en el cuerpo de la respuesta
    response = {
        'statusCode': 200,
        'body': json.dumps(idiomas)
    }
    return response
