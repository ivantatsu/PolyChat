import json
import boto3


def lambda_handler(event, context):
    idiomas = event['idiomas']

    # Crea una instancia del cliente de DynamoDB
    dynamodb = boto3.client('dynamodb')

    for idioma in idiomas:
        response = dynamodb.put_item(
            TableName='languages',
            Item={
                'language_id': {'S': idioma['id']},
                'language_label': {'S': idioma['label']}
            }
        )

    # Retorna una respuesta indicando el éxito de la operación
    response = {
        'statusCode': 200,
        'body': json.dumps('Idiomas insertados exitosamente')
    }
    return response
